package com.widgetg7.mobile.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.widgetg7.mobile.settings.AppSettingsStore

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

        BackgroundSyncFallback.enqueueImmediateSync(context)
    }
}
