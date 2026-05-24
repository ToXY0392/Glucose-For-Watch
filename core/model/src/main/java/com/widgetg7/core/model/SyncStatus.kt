package com.widgetg7.core.model

enum class SyncErrorCategory {
    NONE,
    AUTH,
    NETWORK,
    OTHER,
}

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
    fun hasSuccessfulSync(): Boolean = lastValueMgDl != null && lastSyncEpochMs > 0L
}
