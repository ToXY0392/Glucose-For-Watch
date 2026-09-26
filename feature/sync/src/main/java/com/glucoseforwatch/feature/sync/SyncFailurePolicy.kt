package com.glucoseforwatch.feature.sync

/** User-notification triggers after repeated sync failures. */
enum class SyncNotificationAction {
    DEXCOM_RECONNECT_REQUIRED,
    SYNC_INTERRUPTED,
}

/**
 * Decides whether a failed sync should surface a reconnect or interrupted notification.
 *
 * Notify at exactly two consecutive AUTH errors or three consecutive sync failures.
 */
object SyncFailurePolicy {
    fun decideNotificationAction(
        lastErrorCategory: String,
        authFailureCount: Int,
        consecutiveFailureCount: Int,
    ): SyncNotificationAction? {
        return when {
            lastErrorCategory == "AUTH" ->
                if (authFailureCount == AUTH_FAILURE_NOTIFICATION_THRESHOLD) {
                    SyncNotificationAction.DEXCOM_RECONNECT_REQUIRED
                } else {
                    null
                }

            consecutiveFailureCount == SYNC_FAILURE_NOTIFICATION_THRESHOLD ->
                SyncNotificationAction.SYNC_INTERRUPTED

            else -> null
        }
    }

    private const val AUTH_FAILURE_NOTIFICATION_THRESHOLD = 2
    private const val SYNC_FAILURE_NOTIFICATION_THRESHOLD = 3
}
