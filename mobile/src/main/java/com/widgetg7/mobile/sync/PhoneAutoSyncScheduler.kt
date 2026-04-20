package com.widgetg7.mobile.sync

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock

object PhoneAutoSyncScheduler {
    private const val REQUEST_CODE = 1001
    private const val INTERVAL_MS = 2 * 60 * 1000L

    fun schedule(context: Context, delayMs: Long = INTERVAL_MS) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = pendingIntent(context)
        val triggerAtMillis = SystemClock.elapsedRealtime() + delayMs

        alarmManager.cancel(pendingIntent)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )

            else ->
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )
        }
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, PhoneAutoSyncReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
