package com.widgetg7.mobile.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.widgetg7.mobile.settings.AppSettingsStore

class PhoneBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                val settingsStore = AppSettingsStore(context)
                if (!settingsStore.loadDexcomSettings().isConfigured()) {
                    return
                }
                if (settingsStore.isActiveSyncEnabled()) {
                    ActiveGlucoseSyncController.start(context)
                } else {
                    PhoneAutoSyncScheduler.schedule(context)
                }
            }

            else -> Unit
        }
    }
}
