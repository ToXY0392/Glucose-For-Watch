package com.glucoseforwatch.mobile.sync

import com.glucoseforwatch.core.model.SyncErrorCategory
import com.glucoseforwatch.core.model.SyncStatusSnapshot
import com.glucoseforwatch.feature.sync.SyncFailurePolicy
import com.glucoseforwatch.feature.sync.SyncNotificationAction

/** User message, error category, and optional notification action after sync failure. */
data class PhoneSyncFailureOutcome(
    val message: String,
    val category: SyncErrorCategory,
    val notificationAction: SyncNotificationAction?,
)

/** Classifies sync errors and decides whether to notify the user. */
object PhoneSyncFailureHandler {
    fun evaluate(error: Throwable, currentStatus: SyncStatusSnapshot): PhoneSyncFailureOutcome {
        val message = SyncErrorAdapter.toUserMessage(error)
        val category = SyncErrorAdapter.toCategory(error)

        val nextAuthFailureCount = if (category == SyncErrorCategory.AUTH) {
            currentStatus.authFailureCount + 1
        } else {
            0
        }
        val nextConsecutiveFailureCount = currentStatus.consecutiveFailureCount + 1

        val notificationAction = SyncFailurePolicy.decideNotificationAction(
            lastErrorCategory = category.name,
            authFailureCount = nextAuthFailureCount,
            consecutiveFailureCount = nextConsecutiveFailureCount,
        )

        return PhoneSyncFailureOutcome(
            message = message,
            category = category,
            notificationAction = notificationAction,
        )
    }
}
