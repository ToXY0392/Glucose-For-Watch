package com.glucoseforwatch.mobile.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.glucoseforwatch.mobile.settings.AppSettingsStore

/** Alarm receiver: starts active sync or enqueues a one-shot Worker. */
class PhoneAutoSyncReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (!AppSettingsStore(context).loadDexcomSettings().isConfigured()) {
            return
        }

        if (ActiveGlucoseSyncService.isRunning) {
            // Service already polling: only re-arm the watchdog.
            PhoneAutoSyncScheduler.schedule(context, delayMs = PhoneAutoSyncScheduler.WATCHDOG_INTERVAL_MS)
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
