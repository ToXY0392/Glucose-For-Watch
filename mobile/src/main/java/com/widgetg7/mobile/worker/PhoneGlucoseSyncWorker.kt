package com.widgetg7.mobile.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.widgetg7.feature.sync.SyncExecutionResult
import com.widgetg7.feature.sync.WatchDeliveryStatus
import com.widgetg7.mobile.sync.GlucoseKeys
import com.widgetg7.mobile.sync.PendingPushFlusher
import com.widgetg7.mobile.sync.PendingPushQueue
import com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine

/** WorkManager fallback when the foreground sync service is not running. */
class PhoneGlucoseSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val triggeredFromWatch = inputData.getBoolean(GlucoseKeys.INPUT_TRIGGERED_FROM_WATCH, false)
        if (runAttemptCount >= MAX_RETRY_ATTEMPTS) {
            return Result.failure()
        }

        val pendingQueue = PendingPushQueue(applicationContext)
        if (pendingQueue.hasPending()) {
            PendingPushFlusher.flush(applicationContext)
        }

        val result =
            PhoneGlucoseSyncEngine(applicationContext).run(
                triggeredFromWatch = triggeredFromWatch,
                forcePushCurrentReading = pendingQueue.hasPending(),
            )
        return when (result) {
            is SyncExecutionResult.Failure -> Result.retry()
            is SyncExecutionResult.SuccessNewReading,
            is SyncExecutionResult.SuccessNoNewReading,
            -> {
                when (result.watchDelivery) {
                    WatchDeliveryStatus.DELIVERED,
                    WatchDeliveryStatus.NOT_APPLICABLE,
                    -> Result.success()

                    WatchDeliveryStatus.QUEUED,
                    WatchDeliveryStatus.WATCH_UNAVAILABLE,
                    -> Result.retry()
                }
            }
        }
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 4
    }
}
