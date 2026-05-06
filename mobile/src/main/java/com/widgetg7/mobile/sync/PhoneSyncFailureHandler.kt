package com.widgetg7.mobile.sync

import com.widgetg7.core.model.SyncErrorCategory
import com.widgetg7.core.model.SyncStatusSnapshot
import com.widgetg7.feature.sync.SyncFailurePolicy
import com.widgetg7.feature.sync.SyncNotificationAction

data class PhoneSyncFailureOutcome(
    val message: String,
    val category: SyncErrorCategory,
    val notificationAction: SyncNotificationAction?,
)

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
