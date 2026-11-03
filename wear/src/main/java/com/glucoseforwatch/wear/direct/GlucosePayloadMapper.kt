package com.glucoseforwatch.wear.direct

import com.glucoseforwatch.core.datalayer.GlucoseDataLayerContract
import com.glucoseforwatch.core.datalayer.ReadingSource
import com.glucoseforwatch.core.datalayer.ReadingSourceEnvelopeRejection
import com.glucoseforwatch.core.datalayer.ReadingSourceResolution
import com.glucoseforwatch.core.model.GlucoseReading
import com.glucoseforwatch.core.model.GlucoseSource
import com.glucoseforwatch.core.model.SourcedGlucoseReading

internal enum class GlucosePayloadRejection {
    SOURCE_ENVELOPE_INCOMPLETE,
    SOURCE_ENVELOPE_VERSION_UNSUPPORTED,
    SOURCE_ENVELOPE_SOURCE_UNKNOWN,
    MISSING_REQUIRED_FIELD,
    INVALID_MEASUREMENT_TIMESTAMP,
    INVALID_RECEIVE_TIMESTAMP,
}

internal sealed interface GlucosePayloadMapping {
    data class Accepted(
        val reading: SourcedGlucoseReading,
        val isLegacyPayload: Boolean,
    ) : GlucosePayloadMapping

    data class Rejected(val reason: GlucosePayloadRejection) : GlucosePayloadMapping
}

/** Converts a Share or direct-source payload into the model consumed by the arbiter. */
internal object GlucosePayloadMapper {
    fun map(
        sourceEnvelopeVersion: Int?,
        readingSource: String?,
        valueMgDl: Int?,
        trend: String?,
        deltaMgDl: Int?,
        measurementAtEpochMs: Long?,
        isStale: Boolean?,
        receivedAtEpochMs: Long,
    ): GlucosePayloadMapping {
        val sourceResolution = GlucoseDataLayerContract.resolveReadingSource(
            schemaVersion = sourceEnvelopeVersion,
            source = readingSource,
        )
        val acceptedSource = when (sourceResolution) {
            is ReadingSourceResolution.Accepted -> sourceResolution
            is ReadingSourceResolution.Rejected -> {
                return GlucosePayloadMapping.Rejected(sourceResolution.reason.toPayloadRejection())
            }
        }

        if (valueMgDl == null || trend.isNullOrBlank() || deltaMgDl == null ||
            measurementAtEpochMs == null || isStale == null
        ) {
            return GlucosePayloadMapping.Rejected(GlucosePayloadRejection.MISSING_REQUIRED_FIELD)
        }
        if (measurementAtEpochMs <= 0L) {
            return GlucosePayloadMapping.Rejected(
                GlucosePayloadRejection.INVALID_MEASUREMENT_TIMESTAMP,
            )
        }
        if (receivedAtEpochMs <= 0L) {
            return GlucosePayloadMapping.Rejected(GlucosePayloadRejection.INVALID_RECEIVE_TIMESTAMP)
        }

        return GlucosePayloadMapping.Accepted(
            reading = SourcedGlucoseReading(
                source = acceptedSource.source.toModelSource(),
                reading = GlucoseReading(
                    valueMgDl = valueMgDl,
                    trend = trend,
                    deltaMgDl = deltaMgDl,
                    timestampEpochMs = measurementAtEpochMs,
                    stale = isStale,
                ),
                receivedAtEpochMs = receivedAtEpochMs,
            ),
            isLegacyPayload = acceptedSource.isLegacy,
        )
    }

    private fun ReadingSource.toModelSource(): GlucoseSource = when (this) {
        ReadingSource.SHARE -> GlucoseSource.SHARE
        ReadingSource.DIRECT_G7 -> GlucoseSource.DIRECT_G7
    }

    private fun ReadingSourceEnvelopeRejection.toPayloadRejection(): GlucosePayloadRejection =
        when (this) {
            ReadingSourceEnvelopeRejection.INCOMPLETE_ENVELOPE ->
                GlucosePayloadRejection.SOURCE_ENVELOPE_INCOMPLETE
            ReadingSourceEnvelopeRejection.UNSUPPORTED_VERSION ->
                GlucosePayloadRejection.SOURCE_ENVELOPE_VERSION_UNSUPPORTED
            ReadingSourceEnvelopeRejection.UNKNOWN_SOURCE ->
                GlucosePayloadRejection.SOURCE_ENVELOPE_SOURCE_UNKNOWN
        }
}
