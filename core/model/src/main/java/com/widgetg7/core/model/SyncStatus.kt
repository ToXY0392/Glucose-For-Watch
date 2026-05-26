package com.widgetg7.core.model

/** Category of the most recent sync failure for UI and retry policy. */
enum class SyncErrorCategory {
    NONE,
    AUTH,
    NETWORK,
    OTHER,
}

/** Persisted snapshot of the last Dexcom fetch and watch push state. */
data class SyncStatusSnapshot(
    val lastValueMgDl: Int?,
    val lastTrend: String,
    val lastSourceName: String,
    val lastSyncEpochMs: Long,
    val lastReadingTimestampEpochMs: Long,
    val lastError: String,
    val lastErrorCategory: SyncErrorCategory,
    val authFailureCount: Int,
    val consecutiveFailureCount: Int,
    val watchPushPending: Boolean = false,
) {
    /** True when a glucose value was fetched at least once. */
    fun hasSuccessfulSync(): Boolean = lastValueMgDl != null && lastSyncEpochMs > 0L
}
