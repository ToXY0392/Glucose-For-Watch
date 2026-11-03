package com.glucoseforwatch.core.model

enum class GlucoseSource {
    SHARE,
    DIRECT_G7,
}

data class SourcedGlucoseReading(
    val source: GlucoseSource,
    val reading: GlucoseReading,
    val receivedAtEpochMs: Long,
)

data class GlucoseArbitrationPolicy(
    val shareMaxAgeMs: Long,
    val directG7MaxAgeMs: Long?,
    val maxFutureSkewMs: Long,
) {
    init {
        require(shareMaxAgeMs > 0) { "Share freshness threshold must be positive" }
        require(directG7MaxAgeMs == null || directG7MaxAgeMs > 0) {
            "Direct G7 freshness threshold must be positive when enabled"
        }
        require(maxFutureSkewMs >= 0) { "Future timestamp tolerance cannot be negative" }
    }

    fun maxAgeFor(source: GlucoseSource): Long? = when (source) {
        GlucoseSource.SHARE -> shareMaxAgeMs
        GlucoseSource.DIRECT_G7 -> directG7MaxAgeMs
    }
}

enum class ReadingRejection {
    SOURCE_DISABLED,
    INVALID_TIMESTAMP,
    INVALID_RECEIVE_TIMESTAMP,
    FUTURE_TIMESTAMP,
    FUTURE_RECEIVE_TIMESTAMP,
    OUT_OF_ORDER,
    CONFLICTING_DUPLICATE,
}

sealed interface ReadingAcceptance {
    data object Accepted : ReadingAcceptance
    data object Duplicate : ReadingAcceptance
    data class Rejected(val reason: ReadingRejection) : ReadingAcceptance
}

enum class GlucoseSelectionReason {
    DIRECT_NEWER,
    SHARE_NEWER,
    DIRECT_UNAVAILABLE,
    NO_FRESH_SOURCE,
    UNCHANGED,
}

data class GlucoseSelection(
    val reading: SourcedGlucoseReading?,
    val isStale: Boolean,
    val reason: GlucoseSelectionReason,
)

/**
 * Pure, watch-independent source ordering and selection policy.
 * A null direct freshness threshold explicitly disables direct eligibility.
 * Callers remain responsible for source-specific glucose and trend validation.
 */
class GlucoseSourceArbiter(
    private val policy: GlucoseArbitrationPolicy,
) {
    private val latestBySource = mutableMapOf<GlucoseSource, SourcedGlucoseReading>()
    private var lastSelected: SourcedGlucoseReading? = null

    fun submit(candidate: SourcedGlucoseReading, nowEpochMs: Long): ReadingAcceptance {
        if (policy.maxAgeFor(candidate.source) == null) {
            return ReadingAcceptance.Rejected(ReadingRejection.SOURCE_DISABLED)
        }
        val timestamp = candidate.reading.timestampEpochMs
        if (nowEpochMs <= 0L || timestamp <= 0L) {
            return ReadingAcceptance.Rejected(ReadingRejection.INVALID_TIMESTAMP)
        }
        if (timestamp > nowEpochMs && timestamp - nowEpochMs > policy.maxFutureSkewMs) {
            return ReadingAcceptance.Rejected(ReadingRejection.FUTURE_TIMESTAMP)
        }
        val receivedAt = candidate.receivedAtEpochMs
        if (receivedAt <= 0L) {
            return ReadingAcceptance.Rejected(ReadingRejection.INVALID_RECEIVE_TIMESTAMP)
        }
        if (receivedAt > nowEpochMs && receivedAt - nowEpochMs > policy.maxFutureSkewMs) {
            return ReadingAcceptance.Rejected(ReadingRejection.FUTURE_RECEIVE_TIMESTAMP)
        }

        val sameSource = latestBySource[candidate.source]
        if (sameSource != null) {
            when {
                timestamp < sameSource.reading.timestampEpochMs ->
                    return ReadingAcceptance.Rejected(ReadingRejection.OUT_OF_ORDER)
                timestamp == sameSource.reading.timestampEpochMs ->
                    return if (sameMeasurement(candidate, sameSource)) {
                        ReadingAcceptance.Duplicate
                    } else {
                        ReadingAcceptance.Rejected(ReadingRejection.CONFLICTING_DUPLICATE)
                    }
            }
        }

        latestBySource[candidate.source] = candidate
        return ReadingAcceptance.Accepted
    }

    fun select(nowEpochMs: Long): GlucoseSelection {
        val freshCandidates = latestBySource.values.filter { isFresh(it, nowEpochMs) }
        val selected = freshCandidates.maxWithOrNull(
            compareBy<SourcedGlucoseReading> { it.reading.timestampEpochMs }
                .thenBy { if (it.source == GlucoseSource.DIRECT_G7) 1 else 0 },
        )

        if (selected == null) {
            return GlucoseSelection(
                reading = lastSelected,
                isStale = lastSelected != null,
                reason = GlucoseSelectionReason.NO_FRESH_SOURCE,
            )
        }

        val reason = when {
            lastSelected?.source == selected.source -> GlucoseSelectionReason.UNCHANGED
            selected.source == GlucoseSource.DIRECT_G7 -> GlucoseSelectionReason.DIRECT_NEWER
            latestBySource[GlucoseSource.DIRECT_G7]?.let { isFresh(it, nowEpochMs) } != true ->
                GlucoseSelectionReason.DIRECT_UNAVAILABLE
            else -> GlucoseSelectionReason.SHARE_NEWER
        }
        lastSelected = selected
        return GlucoseSelection(reading = selected, isStale = false, reason = reason)
    }

    private fun isFresh(candidate: SourcedGlucoseReading, nowEpochMs: Long): Boolean {
        val maxAgeMs = policy.maxAgeFor(candidate.source) ?: return false
        val timestamp = candidate.reading.timestampEpochMs
        if (timestamp <= 0L || candidate.reading.stale) return false
        if (timestamp > nowEpochMs && timestamp - nowEpochMs > policy.maxFutureSkewMs) return false
        val ageMs = (nowEpochMs - timestamp).coerceAtLeast(0L)
        return ageMs <= maxAgeMs
    }

    private fun sameMeasurement(
        first: SourcedGlucoseReading,
        second: SourcedGlucoseReading,
    ): Boolean = first.reading == second.reading
}
