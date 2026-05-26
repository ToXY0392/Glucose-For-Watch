package com.widgetg7.mobile.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.widgetg7.mobile.MainActivity
import com.widgetg7.mobile.R
import com.widgetg7.mobile.sync.ActiveGlucoseSyncService

/** Sync alert and foreground-service notification builders. */
class NotificationHelper(private val context: Context) {

    fun notifyDexcomReconnectRequired() {
        if (!canNotify()) return
        ensureChannel()
        val notification = NotificationCompat.Builder(context, CHANNEL_SYNC_STATUS)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Reconnectez votre compte Dexcom")
            .setContentText("La synchronisation ne peut plus se faire tant que vos identifiants Dexcom n'ont pas été vérifiés.")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_DEXCOM_AUTH, notification)
    }

    fun notifySyncInterrupted(message: String) {
        if (!canNotify()) return
        ensureChannel()
        val notification = NotificationCompat.Builder(context, CHANNEL_SYNC_STATUS)
            .setSmallIcon(android.R.drawable.stat_notify_sync_noanim)
            .setContentTitle("Synchronisation interrompue")
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_SYNC_INTERRUPTED, notification)
    }

    fun cancelSyncAlerts() {
        NotificationManagerCompat.from(context).cancel(ID_DEXCOM_AUTH)
        NotificationManagerCompat.from(context).cancel(ID_SYNC_INTERRUPTED)
    }

    fun buildActiveSyncNotification(): android.app.Notification {
        ensureActiveSyncChannel()
        val openIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val syncNowIntent = PendingIntent.getService(
            context,
            1,
            ActiveGlucoseSyncService.syncNowIntent(context),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val stopIntent = PendingIntent.getService(
            context,
            2,
            ActiveGlucoseSyncService.stopIntent(context),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(context, CHANNEL_ACTIVE_SYNC)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(context.getString(R.string.notification_sync_title))
            .setContentText(context.getString(R.string.notification_sync_text))
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.stat_notify_sync, "Synchroniser", syncNowIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Arreter", stopIntent)
            .build()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existing = manager.getNotificationChannel(CHANNEL_SYNC_STATUS)
        if (existing != null) return

        val channel = NotificationChannel(
            CHANNEL_SYNC_STATUS,
            "Etat de synchronisation",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Alertes utiles lorsque Dexcom ou la synchronisation ont besoin de votre attention."
        }
        manager.createNotificationChannel(channel)
    }

    private fun ensureActiveSyncChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existing = manager.getNotificationChannel(CHANNEL_ACTIVE_SYNC)
        if (existing != null) return

        val channel = NotificationChannel(
            CHANNEL_ACTIVE_SYNC,
            "Surveillance glycémie",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Notification permanente utilisee pour garder la synchronisation active."
        }
        manager.createNotificationChannel(channel)
    }

    private fun canNotify(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val CHANNEL_SYNC_STATUS = "sync_status"
        private const val CHANNEL_ACTIVE_SYNC = "active_glucose_sync"
        const val ID_ACTIVE_SYNC = 2001
        private const val ID_DEXCOM_AUTH = 1001
        private const val ID_SYNC_INTERRUPTED = 1002
    }
}
