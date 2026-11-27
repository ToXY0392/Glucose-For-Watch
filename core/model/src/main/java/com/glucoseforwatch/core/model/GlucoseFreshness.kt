package com.glucoseforwatch.core.model

/** Single staleness rule shared by Dexcom fetch, watch cache, tile, complication and watch app. */
object GlucoseFreshness {
    /** Readings arrive every 5 min: stale once about two readings are missing. */
    const val STALE_AFTER_MS = 15 * 60_000L

    fun isStale(timestampEpochMs: Long, nowEpochMs: Long): Boolean =
        timestampEpochMs <= 0L || nowEpochMs - timestampEpochMs > STALE_AFTER_MS
}
