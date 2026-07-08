package com.glucoseforwatch.mobile.notifications

import android.content.Context

/** SharedPreferences-backed [NotificationStateStore] for synchronous alert deduplication. */
class SharedPrefsNotificationStateStore(context: Context) : NotificationStateStore {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getLastAlert(notificationId: Int): LastNotificationAlert? {
        val title = prefs.getString(titleKey(notificationId), null) ?: return null
        val message = prefs.getString(messageKey(notificationId), null) ?: return null
        return LastNotificationAlert(title = title, message = message)
    }

    override fun saveLastAlert(notificationId: Int, title: String, message: String) {
        prefs.edit()
            .putString(titleKey(notificationId), title)
            .putString(messageKey(notificationId), message)
            .apply()
    }

    override fun clearAlert(notificationId: Int) {
        prefs.edit()
            .remove(titleKey(notificationId))
            .remove(messageKey(notificationId))
            .apply()
    }

    override fun clearAllAlerts() {
        prefs.edit().clear().apply()
    }

    private fun titleKey(notificationId: Int): String = KEY_TITLE_PREFIX + notificationId

    private fun messageKey(notificationId: Int): String = KEY_MESSAGE_PREFIX + notificationId

    companion object {
        private const val PREFS_NAME = "gfw_notification_state"
        private const val KEY_TITLE_PREFIX = "last_alert_title_"
        private const val KEY_MESSAGE_PREFIX = "last_alert_message_"
    }
}
