package com.widgetg7.mobile.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.widgetg7.mobile.sync.GlucoseKeys
import com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine
import com.widgetg7.mobile.sync.SyncExecutionResult

class PhoneGlucoseSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val triggeredFromWatch = inputData.getBoolean(GlucoseKeys.INPUT_TRIGGERED_FROM_WATCH, false)

        return when (PhoneGlucoseSyncEngine(applicationContext).run(triggeredFromWatch)) {
            is SyncExecutionResult.Success -> Result.success()
            is SyncExecutionResult.Failure -> Result.retry()
        }
    }
}
