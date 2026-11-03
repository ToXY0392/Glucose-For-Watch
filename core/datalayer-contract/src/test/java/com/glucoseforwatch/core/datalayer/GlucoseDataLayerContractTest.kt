package com.glucoseforwatch.core.datalayer

import org.junit.Assert.assertEquals
import org.junit.Test

class GlucoseDataLayerContractTest {
    @Test
    fun legacy_payload_without_source_envelope_is_share() {
        assertEquals(
            ReadingSourceResolution.Accepted(ReadingSource.SHARE, isLegacy = true),
            GlucoseDataLayerContract.resolveReadingSource(schemaVersion = null, source = null),
        )
    }

    @Test
    fun versioned_share_and_direct_sources_are_resolved() {
        assertEquals(
            ReadingSourceResolution.Accepted(ReadingSource.SHARE, isLegacy = false),
            GlucoseDataLayerContract.resolveReadingSource(
                schemaVersion = GlucoseDataLayerContract.SOURCE_ENVELOPE_VERSION_1,
                source = GlucoseDataLayerContract.SOURCE_SHARE,
            ),
        )
        assertEquals(
            ReadingSourceResolution.Accepted(ReadingSource.DIRECT_G7, isLegacy = false),
            GlucoseDataLayerContract.resolveReadingSource(
                schemaVersion = GlucoseDataLayerContract.SOURCE_ENVELOPE_VERSION_1,
                source = GlucoseDataLayerContract.SOURCE_DIRECT_G7,
            ),
        )
    }

    @Test
    fun rejects_unsupported_version() {
        assertEquals(
            ReadingSourceResolution.Rejected(ReadingSourceEnvelopeRejection.UNSUPPORTED_VERSION),
            GlucoseDataLayerContract.resolveReadingSource(
                schemaVersion = 2,
                source = GlucoseDataLayerContract.SOURCE_SHARE,
            ),
        )
    }

    @Test
    fun rejects_partial_envelopes() {
        assertEquals(
            ReadingSourceResolution.Rejected(ReadingSourceEnvelopeRejection.INCOMPLETE_ENVELOPE),
            GlucoseDataLayerContract.resolveReadingSource(
                schemaVersion = null,
                source = GlucoseDataLayerContract.SOURCE_DIRECT_G7,
            ),
        )
        assertEquals(
            ReadingSourceResolution.Rejected(ReadingSourceEnvelopeRejection.INCOMPLETE_ENVELOPE),
            GlucoseDataLayerContract.resolveReadingSource(
                schemaVersion = GlucoseDataLayerContract.SOURCE_ENVELOPE_VERSION_1,
                source = null,
            ),
        )
    }

    @Test
    fun rejects_unknown_source_values() {
        assertEquals(
            ReadingSourceResolution.Rejected(ReadingSourceEnvelopeRejection.UNKNOWN_SOURCE),
            GlucoseDataLayerContract.resolveReadingSource(
                schemaVersion = GlucoseDataLayerContract.SOURCE_ENVELOPE_VERSION_1,
                source = "UNRECOGNIZED",
            ),
        )
    }
}
