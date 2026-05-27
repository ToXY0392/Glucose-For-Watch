package com.glucoseforwatch.mobile.ui

import android.content.Context
import com.glucoseforwatch.feature.sync.SyncExecutionResult
import com.glucoseforwatch.feature.sync.WatchDeliveryStatus
import com.glucoseforwatch.mobile.R

/** Snackbar text after a manual sync attempt. */
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
