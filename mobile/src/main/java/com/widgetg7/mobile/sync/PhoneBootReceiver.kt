package com.widgetg7.mobile.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PhoneBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                PhoneAutoSyncScheduler.schedule(context)
                if (
                    com.widgetg7.mobile.settings.AppSettingsStore(context).isActiveSyncEnabled() &&
                    com.widgetg7.mobile.settings.AppSettingsStore(context).loadDexcomSettings().isConfigured()
                ) {
                    ActiveGlucoseSyncController.start(context)
                }
            }

            else -> Unit
        }
    }
}
