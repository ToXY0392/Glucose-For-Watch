package com.glucoseforwatch.mobile.sync

import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Single entry for starting active sync: alarm + foreground service, with Worker fallback
 * when Android refuses dataSync FGS (background quota exhausted).
 */
object ActiveGlucoseSyncController {
    private const val TAG = "WG7.ActiveSyncCtrl"

    fun start(context: Context) {
        PhoneAutoSyncScheduler.schedule(context)
        if (!startForegroundServiceSafely(context, ActiveGlucoseSyncService.startIntent(context))) {
            BackgroundSyncFallback.activate(context)
        }
    }

    fun syncNow(context: Context) {
        if (!startForegroundServiceSafely(context, ActiveGlucoseSyncService.syncNowIntent(context))) {
            BackgroundSyncFallback.enqueueImmediateSync(context)
        }
    }

    fun stop(context: Context) {
        context.startService(ActiveGlucoseSyncService.stopIntent(context))
    }

    private fun startForegroundServiceSafely(context: Context, intent: Intent): Boolean {
        return try {
            context.startForegroundService(intent)
            true
        } catch (error: IllegalStateException) {
            if (!ActiveGlucoseSyncForegroundGate.isForegroundStartRefused(error)) throw error
            Log.w(TAG, "fgs_start_blocked action=${intent.action}", error)
            false
        }
    }
}
