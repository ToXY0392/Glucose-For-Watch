package com.widgetg7.core.testing

import com.widgetg7.core.model.GlucoseReading
import com.widgetg7.core.model.SyncErrorCategory
import com.widgetg7.core.model.SyncStatusSnapshot

object SyncTestFixtures {
    fun glucoseReading(
        valueMgDl: Int = 123,
        trend: String = "FLAT",
        deltaMgDl: Int = 0,
        timestampEpochMs: Long = 1_000L,
        stale: Boolean = false,
    ): GlucoseReading = GlucoseReading(
        valueMgDl = valueMgDl,
        trend = trend,
        deltaMgDl = deltaMgDl,
        timestampEpochMs = timestampEpochMs,
        stale = stale,
    )

    fun syncStatusSnapshot(
        lastValueMgDl: Int? = 123,
        lastTrend: String = "FLAT",
        lastSourceName: String = "test",
        lastSyncEpochMs: Long = 0L,
        lastReadingTimestampEpochMs: Long = 0L,
        lastError: String = "",
        lastErrorCategory: SyncErrorCategory = SyncErrorCategory.NONE,
        authFailureCount: Int = 0,
        consecutiveFailureCount: Int = 0,
    ): SyncStatusSnapshot = SyncStatusSnapshot(
        lastValueMgDl = lastValueMgDl,
        lastTrend = lastTrend,
        lastSourceName = lastSourceName,
        lastSyncEpochMs = lastSyncEpochMs,
        lastReadingTimestampEpochMs = lastReadingTimestampEpochMs,
        lastError = lastError,
        lastErrorCategory = lastErrorCategory,
        authFailureCount = authFailureCount,
        consecutiveFailureCount = consecutiveFailureCount,
    )
}
