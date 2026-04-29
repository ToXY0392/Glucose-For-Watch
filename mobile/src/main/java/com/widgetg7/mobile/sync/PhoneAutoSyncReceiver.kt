package com.widgetg7.mobile.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.worker.PhoneGlucoseSyncWorker

class PhoneAutoSyncReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (!AppSettingsStore(context).loadDexcomSettings().isConfigured()) {
            return
        }

        PhoneAutoSyncScheduler.schedule(context)
        val work = OneTimeWorkRequestBuilder<PhoneGlucoseSyncWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "phone-glucose-sync-immediate",
            ExistingWorkPolicy.REPLACE,
            work,
        )
    }
}
