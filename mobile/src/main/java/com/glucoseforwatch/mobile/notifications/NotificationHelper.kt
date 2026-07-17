package com.glucoseforwatch.mobile.notifications

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
import com.glucoseforwatch.mobile.MainActivity
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.sync.ActiveGlucoseSyncService

/** Sync alert and foreground-service notification builders. */
class NotificationHelper @JvmOverloads constructor(
    private val context: Context,
    private val stateStore: NotificationStateStore = SharedPrefsNotificationStateStore(context),
) {

    fun notifyDexcomReconnectRequired() {
        val title = context.getString(R.string.notification_dexcom_reconnect_title)
        val message = context.getString(R.string.notification_dexcom_reconnect_message)
        postSyncAlertIfNeeded(
            notificationId = ID_DEXCOM_AUTH,
            title = title,
            message = message,
            smallIcon = android.R.drawable.stat_notify_error,
        )
    }

    fun notifySyncInterrupted(message: String) {
        postSyncAlertIfNeeded(
            notificationId = ID_SYNC_INTERRUPTED,
            title = context.getString(R.string.notification_sync_interrupted_title),
            message = message,
            smallIcon = android.R.drawable.stat_notify_sync_noanim,
        )
    }

    fun cancelSyncAlerts() {
        NotificationManagerCompat.from(context).cancel(ID_DEXCOM_AUTH)
        NotificationManagerCompat.from(context).cancel(ID_SYNC_INTERRUPTED)
        stateStore.clearAllAlerts()
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
            .addAction(android.R.drawable.stat_notify_sync, context.getString(R.string.notification_action_sync), syncNowIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(R.string.notification_action_stop), stopIntent)
            .build()
    }

    private fun postSyncAlertIfNeeded(
        notificationId: Int,
        title: String,
        message: String,
        smallIcon: Int,
    ) {
        if (!canNotify()) return
        if (isDuplicateActiveAlert(notificationId, title, message)) return

        ensureChannel()
        val notification = NotificationCompat.Builder(context, CHANNEL_SYNC_STATUS)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .build()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
        stateStore.saveLastAlert(notificationId, title, message)
    }

    private fun isDuplicateActiveAlert(
        notificationId: Int,
        title: String,
        message: String,
    ): Boolean {
        val lastAlert = stateStore.getLastAlert(notificationId) ?: return false
        if (lastAlert.title != title || lastAlert.message != message) return false
        return isNotificationActive(notificationId)
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_SYNC_STATUS) != null) return

        val channel = NotificationChannel(
            CHANNEL_SYNC_STATUS,
            context.getString(R.string.notification_channel_sync_status),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_channel_sync_status_desc)
        }
        manager.createNotificationChannel(channel)
    }

    private fun ensureActiveSyncChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ACTIVE_SYNC) != null) return

        val channel = NotificationChannel(
            CHANNEL_ACTIVE_SYNC,
            context.getString(R.string.notification_channel_active_sync),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = context.getString(R.string.notification_channel_active_sync_desc)
        }
        manager.createNotificationChannel(channel)
    }

    private fun canNotify(): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        if (!notificationManager.areNotificationsEnabled()) return false
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun isNotificationActive(id: Int): Boolean {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager.activeNotifications.any { it.id == id }
    }

    companion object {
        private const val CHANNEL_SYNC_STATUS = "sync_status"
        private const val CHANNEL_ACTIVE_SYNC = "active_glucose_sync"
        const val ID_ACTIVE_SYNC = 2001
        private const val ID_DEXCOM_AUTH = 1001
        private const val ID_SYNC_INTERRUPTED = 1002
    }
}
