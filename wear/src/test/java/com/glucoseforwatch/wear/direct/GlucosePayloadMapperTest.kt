package com.glucoseforwatch.wear.direct

import com.glucoseforwatch.core.datalayer.GlucoseDataLayerContract
import com.glucoseforwatch.core.model.GlucoseArbitrationPolicy
import com.glucoseforwatch.core.model.GlucoseReading
import com.glucoseforwatch.core.model.GlucoseSource
import com.glucoseforwatch.core.model.GlucoseSourceArbiter
import com.glucoseforwatch.core.model.ReadingAcceptance
import com.glucoseforwatch.core.model.SourcedGlucoseReading
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlucosePayloadMapperTest {
    @Test
    fun legacy_payload_maps_to_share_with_measurement_and_receive_times() {
        val result = GlucosePayloadMapper.map(
            sourceEnvelopeVersion = null,
            readingSource = null,
            valueMgDl = 110,
            trend = "FLAT",
            deltaMgDl = 0,
            measurementAtEpochMs = 10_000L,
            isStale = false,
            receivedAtEpochMs = 10_250L,
        )

        assertEquals(
            GlucosePayloadMapping.Accepted(
                reading = SourcedGlucoseReading(
                    source = GlucoseSource.SHARE,
                    reading = GlucoseReading(
                        valueMgDl = 110,
                        trend = "FLAT",
                        deltaMgDl = 0,
                        timestampEpochMs = 10_000L,
                        stale = false,
                    ),
                    receivedAtEpochMs = 10_250L,
                ),
                isLegacyPayload = true,
            ),
            result,
        )
    }

    @Test
    fun versioned_direct_payload_maps_to_direct_source() {
        val result = GlucosePayloadMapper.map(
            sourceEnvelopeVersion = GlucoseDataLayerContract.SOURCE_ENVELOPE_VERSION_1,
            readingSource = GlucoseDataLayerContract.SOURCE_DIRECT_G7,
            valueMgDl = 110,
            trend = "FLAT",
            deltaMgDl = 0,
            measurementAtEpochMs = 10_000L,
            isStale = false,
            receivedAtEpochMs = 10_250L,
        )

        val accepted = result as GlucosePayloadMapping.Accepted
        assertEquals(GlucoseSource.DIRECT_G7, accepted.reading.source)
        assertEquals(10_000L, accepted.reading.reading.timestampEpochMs)
        assertEquals(10_250L, accepted.reading.receivedAtEpochMs)
        assertEquals(false, accepted.isLegacyPayload)
    }

    @Test
    fun partial_or_unknown_source_envelopes_are_rejected() {
        assertEquals(
            GlucosePayloadMapping.Rejected(GlucosePayloadRejection.SOURCE_ENVELOPE_INCOMPLETE),
            mapPayload(sourceEnvelopeVersion = null, readingSource = "DIRECT_G7"),
        )
        assertEquals(
            GlucosePayloadMapping.Rejected(GlucosePayloadRejection.SOURCE_ENVELOPE_VERSION_UNSUPPORTED),
            mapPayload(sourceEnvelopeVersion = 2, readingSource = "SHARE"),
        )
        assertEquals(
            GlucosePayloadMapping.Rejected(GlucosePayloadRejection.SOURCE_ENVELOPE_SOURCE_UNKNOWN),
            mapPayload(sourceEnvelopeVersion = 1, readingSource = "OTHER"),
        )
    }

    @Test
    fun missing_fields_and_invalid_timestamps_are_rejected() {
        assertEquals(
            GlucosePayloadMapping.Rejected(GlucosePayloadRejection.MISSING_REQUIRED_FIELD),
            mapPayload(valueMgDl = null),
        )
        assertEquals(
            GlucosePayloadMapping.Rejected(GlucosePayloadRejection.INVALID_MEASUREMENT_TIMESTAMP),
            mapPayload(measurementAtEpochMs = 0L),
        )
        assertEquals(
            GlucosePayloadMapping.Rejected(GlucosePayloadRejection.INVALID_RECEIVE_TIMESTAMP),
            mapPayload(receivedAtEpochMs = 0L),
        )
    }

    @Test
    fun values_outside_the_displayable_range_are_rejected() {
        listOf(-5, 0, 39, 401, 9_999).forEach { value ->
            assertEquals(
                "valueMgDl=$value",
                GlucosePayloadMapping.Rejected(GlucosePayloadRejection.INVALID_VALUE),
                mapPayload(valueMgDl = value),
            )
        }
        listOf(40, 400).forEach { value ->
            assertTrue("valueMgDl=$value", mapPayload(valueMgDl = value) is GlucosePayloadMapping.Accepted)
        }
    }

    @Test
    fun trends_outside_the_wire_vocabulary_are_rejected() {
        listOf("XYZ", "DOUBLE_UP", "flat").forEach { trend ->
            assertEquals(
                "trend=$trend",
                GlucosePayloadMapping.Rejected(GlucosePayloadRejection.INVALID_TREND),
                mapPayload(trend = trend),
            )
        }
        listOf("UP", "UP_RIGHT", "FLAT", "DOWN_RIGHT", "DOWN").forEach { trend ->
            assertTrue("trend=$trend", mapPayload(trend = trend) is GlucosePayloadMapping.Accepted)
        }
    }

    @Test
    fun legacy_payload_maps_to_share_and_is_accepted_by_the_arbiter() {
        val result = mapPayload()
        val mapped = result as GlucosePayloadMapping.Accepted
        val arbiter = GlucoseSourceArbiter(
            GlucoseArbitrationPolicy(
                shareMaxAgeMs = 1_000L,
                directG7MaxAgeMs = null,
                maxFutureSkewMs = 0L,
            ),
        )

        assertEquals(
            ReadingAcceptance.Accepted,
            arbiter.submit(mapped.reading, nowEpochMs = 10_250L),
        )
        assertEquals(mapped.reading, arbiter.select(nowEpochMs = 10_250L).reading)
    }

    private fun mapPayload(
        sourceEnvelopeVersion: Int? = null,
        readingSource: String? = null,
        valueMgDl: Int? = 110,
        trend: String? = "FLAT",
        measurementAtEpochMs: Long? = 10_000L,
        receivedAtEpochMs: Long = 10_250L,
    ) = GlucosePayloadMapper.map(
        sourceEnvelopeVersion = sourceEnvelopeVersion,
        readingSource = readingSource,
        valueMgDl = valueMgDl,
        trend = trend,
        deltaMgDl = 0,
        measurementAtEpochMs = measurementAtEpochMs,
        isStale = false,
        receivedAtEpochMs = receivedAtEpochMs,
    )
}
