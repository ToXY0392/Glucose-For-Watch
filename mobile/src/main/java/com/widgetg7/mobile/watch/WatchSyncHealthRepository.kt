package com.widgetg7.mobile.watch

import android.content.Context

data class WatchSyncHealthStatus(
    val batteryLevel: Int,
    val lowPowerMode: Boolean,
    val syncLimited: Boolean,
    val message: String,
    val updatedAtEpochMs: Long,
    val manufacturer: String,
    val model: String,
    val device: String,
) {
    fun isFresh(nowEpochMs: Long = System.currentTimeMillis()): Boolean {
        if (updatedAtEpochMs <= 0L) return false
        return nowEpochMs - updatedAtEpochMs <= FRESHNESS_MS
    }

    fun summary(): String? {
        if (!isFresh()) return null
        return when {
            syncLimited && batteryLevel in 0..100 -> "Montre: $message ($batteryLevel%)"
            syncLimited -> "Montre: $message"
            batteryLevel in 0..20 -> "Montre: batterie faible ($batteryLevel%)"
            else -> null
        }
    }

    companion object {
        private const val FRESHNESS_MS = 6 * 60 * 60 * 1000L
    }
}

class WatchSyncHealthRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(status: WatchSyncHealthStatus) {
        prefs.edit()
            .putInt(KEY_BATTERY_LEVEL, status.batteryLevel)
            .putBoolean(KEY_LOW_POWER, status.lowPowerMode)
            .putBoolean(KEY_SYNC_LIMITED, status.syncLimited)
            .putString(KEY_MESSAGE, status.message)
            .putLong(KEY_UPDATED_AT, status.updatedAtEpochMs)
            .putString(KEY_MANUFACTURER, status.manufacturer)
            .putString(KEY_MODEL, status.model)
            .putString(KEY_DEVICE, status.device)
            .apply()
    }

    fun load(): WatchSyncHealthStatus? {
        if (!prefs.contains(KEY_UPDATED_AT)) return null
        return WatchSyncHealthStatus(
            batteryLevel = prefs.getInt(KEY_BATTERY_LEVEL, -1),
            lowPowerMode = prefs.getBoolean(KEY_LOW_POWER, false),
            syncLimited = prefs.getBoolean(KEY_SYNC_LIMITED, false),
            message = prefs.getString(KEY_MESSAGE, "").orEmpty(),
            updatedAtEpochMs = prefs.getLong(KEY_UPDATED_AT, 0L),
            manufacturer = prefs.getString(KEY_MANUFACTURER, "").orEmpty(),
            model = prefs.getString(KEY_MODEL, "").orEmpty(),
            device = prefs.getString(KEY_DEVICE, "").orEmpty(),
        ).takeIf { it.isFresh() }
    }

    companion object {
        private const val PREFS_NAME = "widget_g7_watch_health"
        private const val KEY_BATTERY_LEVEL = "battery_level"
        private const val KEY_LOW_POWER = "low_power"
        private const val KEY_SYNC_LIMITED = "sync_limited"
        private const val KEY_MESSAGE = "message"
        private const val KEY_UPDATED_AT = "updated_at"
        private const val KEY_MANUFACTURER = "manufacturer"
        private const val KEY_MODEL = "model"
        private const val KEY_DEVICE = "device"
    }
}
