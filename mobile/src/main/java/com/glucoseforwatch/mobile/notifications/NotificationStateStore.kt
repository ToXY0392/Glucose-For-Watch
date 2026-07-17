package com.glucoseforwatch.mobile.notifications

/** Last alert content posted for a given notification id. */
data class LastNotificationAlert(
    val title: String,
    val message: String,
)

/** Persists sync alert notification state across process restarts. */
interface NotificationStateStore {
    fun getLastAlert(notificationId: Int): LastNotificationAlert?

    fun saveLastAlert(notificationId: Int, title: String, message: String)

    fun clearAlert(notificationId: Int)

    fun clearAllAlerts()
}
