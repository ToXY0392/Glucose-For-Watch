package com.widgetg7.mobile.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.worker.PhoneGlucoseSyncWorker
import java.util.concurrent.TimeUnit

class PhoneAutoSyncReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (!AppSettingsStore(context).loadDexcomSettings().isConfigured()) {
            return
        }

        val settingsStore = AppSettingsStore(context)
        PhoneAutoSyncScheduler.schedule(context)
        if (settingsStore.isActiveSyncEnabled()) {
            ActiveGlucoseSyncController.start(context)
            return
        }

        val work = OneTimeWorkRequestBuilder<PhoneGlucoseSyncWorker>()
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "phone-glucose-sync-immediate",
            ExistingWorkPolicy.REPLACE,
            work,
        )
    }
}
