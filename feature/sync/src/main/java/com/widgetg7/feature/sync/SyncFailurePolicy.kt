package com.widgetg7.feature.sync

enum class SyncNotificationAction {
    DEXCOM_RECONNECT_REQUIRED,
    SYNC_INTERRUPTED,
}

object SyncFailurePolicy {
    fun decideNotificationAction(
        lastErrorCategory: String,
        authFailureCount: Int,
        consecutiveFailureCount: Int,
    ): SyncNotificationAction? {
        return when {
            lastErrorCategory == "AUTH" && authFailureCount >= 2 ->
                SyncNotificationAction.DEXCOM_RECONNECT_REQUIRED

            consecutiveFailureCount >= 3 ->
                SyncNotificationAction.SYNC_INTERRUPTED

            else -> null
        }
    }
}
