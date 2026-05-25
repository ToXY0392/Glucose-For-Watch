package com.widgetg7.mobile.sync

import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.content.Intent
import android.os.Build
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            true
        } catch (error: ForegroundServiceStartNotAllowedException) {
            Log.w(TAG, "fgs_start_blocked action=${intent.action}", error)
            false
        }
    }
}
