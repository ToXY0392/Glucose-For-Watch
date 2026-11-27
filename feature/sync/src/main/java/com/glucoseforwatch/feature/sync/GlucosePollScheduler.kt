package com.glucoseforwatch.feature.sync

/**
 * Next Dexcom Share poll delay, aligned on the 5-minute CGM cadence.
 *
 * After a reading, wait until the next one is due (plus an upload margin); only an overdue
 * reading is polled at the short [overdueIntervalMs] cadence. Watch-initiated refreshes bypass
 * this schedule.
 */
object GlucosePollScheduler {
    const val READING_PERIOD_MS = 5 * 60_000L
    const val UPLOAD_MARGIN_MS = 15_000L
    private const val MAX_WAIT_MS = READING_PERIOD_MS + UPLOAD_MARGIN_MS

    fun nextDelayMs(
        nowEpochMs: Long,
        lastReadingTimestampEpochMs: Long,
        overdueIntervalMs: Long,
    ): Long {
        if (lastReadingTimestampEpochMs <= 0L) return overdueIntervalMs
        val untilExpected = lastReadingTimestampEpochMs + MAX_WAIT_MS - nowEpochMs
        if (untilExpected <= 0L) return overdueIntervalMs
        return untilExpected.coerceAtMost(MAX_WAIT_MS)
    }
}
