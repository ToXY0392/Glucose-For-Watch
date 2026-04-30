package com.widgetg7.mobile.sync

import android.content.Context
import android.os.Build

object ActiveGlucoseSyncController {
    fun start(context: Context) {
        val intent = ActiveGlucoseSyncService.startIntent(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        PhoneAutoSyncScheduler.schedule(context)
    }

    fun syncNow(context: Context) {
        val intent = ActiveGlucoseSyncService.syncNowIntent(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stop(context: Context) {
        context.startService(ActiveGlucoseSyncService.stopIntent(context))
    }
}
