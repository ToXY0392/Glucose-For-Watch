package com.widgetg7.mobile.ui

import android.content.Context
import com.widgetg7.feature.sync.SyncExecutionResult
import com.widgetg7.feature.sync.WatchDeliveryStatus
import com.widgetg7.mobile.R

object ManualSyncFeedbackFormatter {
    fun format(context: Context, result: SyncExecutionResult): String =
        when (result) {
            is SyncExecutionResult.Failure ->
                context.getString(R.string.manual_sync_error, result.message)
            is SyncExecutionResult.SuccessNewReading,
            is SyncExecutionResult.SuccessNoNewReading,
            ->
                when (result.watchDelivery) {
                    WatchDeliveryStatus.DELIVERED,
                    WatchDeliveryStatus.NOT_APPLICABLE,
                    -> context.getString(R.string.manual_sync_success)
                    WatchDeliveryStatus.QUEUED,
                    WatchDeliveryStatus.WATCH_UNAVAILABLE,
                    -> context.getString(R.string.manual_sync_pending)
                }
        }
}
