package com.glucoseforwatch.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GlucoseSourceArbiterTest {
    @Test
    fun newer_fresh_measurement_wins_and_direct_wins_timestamp_ties() {
        val arbiter = arbiter()
        val share = candidate(GlucoseSource.SHARE, timestamp = 9_000L)
        val direct = candidate(GlucoseSource.DIRECT_G7, timestamp = 10_000L)

        assertSame(ReadingAcceptance.Accepted, arbiter.submit(share, nowEpochMs = 10_000L))
        assertSame(ReadingAcceptance.Accepted, arbiter.submit(direct, nowEpochMs = 10_000L))
        assertEquals(direct, arbiter.select(nowEpochMs = 10_000L).reading)

        val tiedArbiter = arbiter()
        val tiedShare = candidate(GlucoseSource.SHARE, timestamp = 10_000L)
        val tiedDirect = candidate(GlucoseSource.DIRECT_G7, timestamp = 10_000L)
        tiedArbiter.submit(tiedShare, nowEpochMs = 10_000L)
        tiedArbiter.submit(tiedDirect, nowEpochMs = 10_000L)
        assertEquals(tiedDirect, tiedArbiter.select(nowEpochMs = 10_000L).reading)
    }

    @Test
    fun older_cross_source_value_is_accepted_when_newer_source_is_stale() {
        val arbiter = arbiter(shareMaxAgeMs = 20_000L, directMaxAgeMs = 1_000L)
        val direct = candidate(GlucoseSource.DIRECT_G7, timestamp = 10_000L)
        val share = candidate(GlucoseSource.SHARE, timestamp = 9_000L)

        assertSame(ReadingAcceptance.Accepted, arbiter.submit(direct, nowEpochMs = 11_001L))
        assertSame(ReadingAcceptance.Accepted, arbiter.submit(share, nowEpochMs = 11_001L))

        val selection = arbiter.select(nowEpochMs = 11_001L)
        assertEquals(share, selection.reading)
        assertEquals(GlucoseSelectionReason.DIRECT_UNAVAILABLE, selection.reason)
    }

    @Test
    fun older_share_does_not_displace_a_newer_fresh_direct_reading() {
        val arbiter = arbiter()
        val direct = candidate(GlucoseSource.DIRECT_G7, timestamp = 10_000L)
        val share = candidate(GlucoseSource.SHARE, timestamp = 9_000L)
        arbiter.submit(direct, nowEpochMs = 10_000L)
        arbiter.submit(share, nowEpochMs = 10_000L)

        assertEquals(direct, arbiter.select(nowEpochMs = 10_000L).reading)
    }

    @Test
    fun source_selection_table_handles_missing_and_stale_candidates() {
        data class Scenario(
            val directTimestamp: Long?,
            val shareTimestamp: Long?,
            val expectedSource: GlucoseSource?,
        )

        val scenarios = listOf(
            Scenario(
                directTimestamp = 10_000L,
                shareTimestamp = 9_000L,
                expectedSource = GlucoseSource.DIRECT_G7,
            ),
            Scenario(
                directTimestamp = 10_000L,
                shareTimestamp = null,
                expectedSource = GlucoseSource.DIRECT_G7,
            ),
            Scenario(
                directTimestamp = 8_000L,
                shareTimestamp = 9_000L,
                expectedSource = GlucoseSource.SHARE,
            ),
            Scenario(
                directTimestamp = null,
                shareTimestamp = 9_000L,
                expectedSource = GlucoseSource.SHARE,
            ),
            Scenario(directTimestamp = 8_000L, shareTimestamp = 7_999L, expectedSource = null),
        )

        scenarios.forEach { scenario ->
            val arbiter = arbiter(shareMaxAgeMs = 2_000L, directMaxAgeMs = 1_000L)
            scenario.directTimestamp?.let {
                arbiter.submit(candidate(GlucoseSource.DIRECT_G7, it), nowEpochMs = 10_000L)
            }
            scenario.shareTimestamp?.let {
                arbiter.submit(candidate(GlucoseSource.SHARE, it), nowEpochMs = 10_000L)
            }

            val selection = arbiter.select(nowEpochMs = 10_000L)
            assertEquals(scenario.expectedSource, selection.reading?.source)
            assertEquals(scenario.expectedSource == null, selection.reading == null)
            assertFalse(selection.isStale)
        }
    }

    @Test
    fun direct_recovery_selects_new_valid_direct_measurement_after_share_fallback() {
        val arbiter = arbiter(shareMaxAgeMs = 20_000L, directMaxAgeMs = 2_000L)
        val oldDirect = candidate(GlucoseSource.DIRECT_G7, timestamp = 1_000L)
        val share = candidate(GlucoseSource.SHARE, timestamp = 9_000L)
        arbiter.submit(oldDirect, nowEpochMs = 10_000L)
        arbiter.submit(share, nowEpochMs = 10_000L)
        assertEquals(share, arbiter.select(nowEpochMs = 10_000L).reading)

        val recoveredDirect = candidate(GlucoseSource.DIRECT_G7, timestamp = 10_100L)
        assertSame(
            ReadingAcceptance.Accepted,
            arbiter.submit(recoveredDirect, nowEpochMs = 10_100L),
        )
        val selection = arbiter.select(nowEpochMs = 10_100L)
        assertEquals(recoveredDirect, selection.reading)
        assertEquals(GlucoseSelectionReason.DIRECT_NEWER, selection.reason)
    }

    @Test
    fun older_measurement_from_same_source_is_still_rejected() {
        val arbiter = arbiter()
        arbiter.submit(candidate(GlucoseSource.SHARE, timestamp = 10_000L), nowEpochMs = 10_000L)

        assertEquals(
            ReadingAcceptance.Rejected(ReadingRejection.OUT_OF_ORDER),
            arbiter.submit(candidate(GlucoseSource.SHARE, timestamp = 9_000L), nowEpochMs = 10_000L),
        )
    }

    @Test
    fun identical_measurement_is_deduplicated_but_conflicting_duplicate_is_rejected() {
        val arbiter = arbiter()
        val original = candidate(GlucoseSource.SHARE, timestamp = 10_000L)
        arbiter.submit(original, nowEpochMs = 10_000L)

        assertSame(ReadingAcceptance.Duplicate, arbiter.submit(original, nowEpochMs = 10_001L))
        val conflicting = original.copy(reading = original.reading.copy(valueMgDl = 123))
        assertEquals(
            ReadingAcceptance.Rejected(ReadingRejection.CONFLICTING_DUPLICATE),
            arbiter.submit(conflicting, nowEpochMs = 10_001L),
        )
    }

    @Test
    fun source_specific_out_of_order_readings_are_rejected() {
        val arbiter = arbiter(directMaxAgeMs = 20_000L)
        arbiter.submit(candidate(GlucoseSource.SHARE, timestamp = 10_000L), nowEpochMs = 10_000L)

        assertEquals(
            ReadingAcceptance.Rejected(ReadingRejection.OUT_OF_ORDER),
            arbiter.submit(candidate(GlucoseSource.SHARE, timestamp = 9_000L), nowEpochMs = 10_000L),
        )
    }

    @Test
    fun no_fresh_source_retains_last_selection_as_stale() {
        val arbiter = arbiter(shareMaxAgeMs = 1_000L)
        val share = candidate(GlucoseSource.SHARE, timestamp = 10_000L)
        arbiter.submit(share, nowEpochMs = 10_000L)
        assertFalse(arbiter.select(nowEpochMs = 10_000L).isStale)

        val stale = arbiter.select(nowEpochMs = 11_001L)
        assertEquals(share, stale.reading)
        assertTrue(stale.isStale)
        assertEquals(GlucoseSelectionReason.NO_FRESH_SOURCE, stale.reason)
    }

    @Test
    fun direct_source_is_ineligible_until_a_threshold_is_configured() {
        val arbiter = arbiter(directMaxAgeMs = null)
        val direct = candidate(GlucoseSource.DIRECT_G7, timestamp = 10_000L)
        assertEquals(
            ReadingAcceptance.Rejected(ReadingRejection.SOURCE_DISABLED),
            arbiter.submit(direct, nowEpochMs = 10_000L),
        )

        val selection = arbiter.select(nowEpochMs = 10_000L)
        assertNull(selection.reading)
        assertFalse(selection.isStale)
        assertEquals(GlucoseSelectionReason.NO_FRESH_SOURCE, selection.reason)
    }

    @Test
    fun stale_input_is_not_selected_even_when_its_timestamp_is_recent() {
        val arbiter = arbiter()
        val staleShare = candidate(GlucoseSource.SHARE, timestamp = 10_000L).let {
            it.copy(reading = it.reading.copy(stale = true))
        }
        arbiter.submit(staleShare, nowEpochMs = 10_000L)

        assertNull(arbiter.select(nowEpochMs = 10_000L).reading)
    }

    @Test
    fun invalid_and_excessively_future_timestamps_are_rejected() {
        val arbiter = arbiter(maxFutureSkewMs = 100L)

        assertEquals(
            ReadingAcceptance.Rejected(ReadingRejection.INVALID_TIMESTAMP),
            arbiter.submit(candidate(GlucoseSource.SHARE, timestamp = 0L), nowEpochMs = 10_000L),
        )
        assertEquals(
            ReadingAcceptance.Rejected(ReadingRejection.FUTURE_TIMESTAMP),
            arbiter.submit(candidate(GlucoseSource.SHARE, timestamp = 10_101L), nowEpochMs = 10_000L),
        )
        assertSame(
            ReadingAcceptance.Accepted,
            arbiter.submit(candidate(GlucoseSource.SHARE, timestamp = 10_100L), nowEpochMs = 10_000L),
        )
    }

    @Test
    fun freshness_threshold_is_inclusive_and_expires_immediately_after_boundary() {
        val arbiter = arbiter(shareMaxAgeMs = 1_000L)
        val share = candidate(GlucoseSource.SHARE, timestamp = 10_000L)
        arbiter.submit(share, nowEpochMs = 10_000L)

        assertEquals(share, arbiter.select(nowEpochMs = 11_000L).reading)
        val expired = arbiter.select(nowEpochMs = 11_001L)
        assertEquals(share, expired.reading)
        assertTrue(expired.isStale)
    }

    @Test
    fun future_receive_timestamp_beyond_tolerance_is_rejected() {
        val arbiter = arbiter(maxFutureSkewMs = 100L)
        val candidate = candidate(GlucoseSource.SHARE, timestamp = 10_000L)
            .copy(receivedAtEpochMs = 10_101L)

        assertEquals(
            ReadingAcceptance.Rejected(ReadingRejection.FUTURE_RECEIVE_TIMESTAMP),
            arbiter.submit(candidate, nowEpochMs = 10_000L),
        )
    }

    @Test
    fun invalid_local_receive_timestamp_is_rejected() {
        val arbiter = arbiter()
        val candidate = candidate(GlucoseSource.SHARE, timestamp = 10_000L)
            .copy(receivedAtEpochMs = 0L)

        assertEquals(
            ReadingAcceptance.Rejected(ReadingRejection.INVALID_RECEIVE_TIMESTAMP),
            arbiter.submit(candidate, nowEpochMs = 10_000L),
        )
    }

    @Test
    fun reason_reports_share_fallback_when_direct_is_not_fresh() {
        val arbiter = arbiter(directMaxAgeMs = 2_000L)
        val direct = candidate(GlucoseSource.DIRECT_G7, timestamp = 1_000L)
        val share = candidate(GlucoseSource.SHARE, timestamp = 10_000L)
        arbiter.submit(direct, nowEpochMs = 10_000L)
        arbiter.submit(share, nowEpochMs = 10_000L)

        val selection = arbiter.select(nowEpochMs = 10_000L)
        assertEquals(share, selection.reading)
        assertEquals(GlucoseSelectionReason.DIRECT_UNAVAILABLE, selection.reason)
    }

    private fun arbiter(
        shareMaxAgeMs: Long = 20_000L,
        directMaxAgeMs: Long? = 20_000L,
        maxFutureSkewMs: Long = 0L,
    ) = GlucoseSourceArbiter(
        GlucoseArbitrationPolicy(
            shareMaxAgeMs = shareMaxAgeMs,
            directG7MaxAgeMs = directMaxAgeMs,
            maxFutureSkewMs = maxFutureSkewMs,
        ),
    )

    private fun candidate(
        source: GlucoseSource,
        timestamp: Long,
    ) = SourcedGlucoseReading(
        source = source,
        reading = GlucoseReading(
            valueMgDl = 100,
            trend = "FLAT",
            deltaMgDl = 0,
            timestampEpochMs = timestamp,
            stale = false,
        ),
        receivedAtEpochMs = timestamp,
    )
}
