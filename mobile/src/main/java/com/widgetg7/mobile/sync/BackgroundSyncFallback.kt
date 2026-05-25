package com.widgetg7.mobile.sync

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.widgetg7.mobile.worker.PhoneGlucoseSyncWorker
import java.util.concurrent.TimeUnit

/** WorkManager + alarm when foreground dataSync service cannot start (quota / background). */
object BackgroundSyncFallback {
    private const val TAG = "WG7.SyncFallback"
    const val WORK_NAME_IMMEDIATE = "phone-glucose-sync-immediate"

    fun activate(context: Context) {
        Log.i(TAG, "activate_fgs_unavailable")
        PhoneSyncStateStore(context).recordActiveServiceState("fgs_unavailable")
        PhoneAutoSyncScheduler.schedule(context)
        enqueueImmediateSync(context)
    }

    fun enqueueImmediateSync(context: Context) {
        val work =
            OneTimeWorkRequestBuilder<PhoneGlucoseSyncWorker>()
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
                .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_IMMEDIATE,
            ExistingWorkPolicy.REPLACE,
            work,
        )
    }
}
