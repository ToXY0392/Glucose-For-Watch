package com.widgetg7.mobile.watch

import android.content.Context
import com.widgetg7.mobile.R

/** Last watch-reported battery, app install, and capability snapshot. */
data class WatchSyncHealthStatus(
    val batteryLevel: Int,
    val isCharging: Boolean,
    val lowPowerMode: Boolean,
    val syncLimited: Boolean,
    val message: String,
    val updatedAtEpochMs: Long,
    val manufacturer: String,
    val model: String,
    val device: String,
    val appInstalled: Boolean,
    val appVersionName: String,
    val appVersionCode: Long,
    val supportsTile: Boolean,
    val supportsComplication: Boolean,
    val ackFailureCount: Int = 0,
) {
    fun isFresh(nowEpochMs: Long = System.currentTimeMillis()): Boolean {
        if (updatedAtEpochMs <= 0L) return false
        return nowEpochMs - updatedAtEpochMs <= FRESHNESS_MS
    }

    fun summary(context: Context): String? {
        if (!isFresh()) return null
        return when {
            appInstalled && appVersionName.isNotBlank() ->
                context.getString(R.string.watch_health_installed_version, appVersionName)
            appInstalled -> context.getString(R.string.watch_health_installed)
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

/** Persists watch health payloads received via the Data Layer. */
class WatchSyncHealthRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(status: WatchSyncHealthStatus) {
        prefs.edit()
            .putInt(KEY_BATTERY_LEVEL, status.batteryLevel)
            .putBoolean(KEY_IS_CHARGING, status.isCharging)
            .putBoolean(KEY_LOW_POWER, status.lowPowerMode)
            .putBoolean(KEY_SYNC_LIMITED, status.syncLimited)
            .putString(KEY_MESSAGE, status.message)
            .putLong(KEY_UPDATED_AT, status.updatedAtEpochMs)
            .putString(KEY_MANUFACTURER, status.manufacturer)
            .putString(KEY_MODEL, status.model)
            .putString(KEY_DEVICE, status.device)
            .putBoolean(KEY_APP_INSTALLED, status.appInstalled)
            .putString(KEY_APP_VERSION_NAME, status.appVersionName)
            .putLong(KEY_APP_VERSION_CODE, status.appVersionCode)
            .putBoolean(KEY_SUPPORTS_TILE, status.supportsTile)
            .putBoolean(KEY_SUPPORTS_COMPLICATION, status.supportsComplication)
            .putInt(KEY_ACK_FAILURE_COUNT, status.ackFailureCount)
            .apply()
    }

    fun load(): WatchSyncHealthStatus? {
        if (!prefs.contains(KEY_UPDATED_AT)) return null
        return WatchSyncHealthStatus(
            batteryLevel = prefs.getInt(KEY_BATTERY_LEVEL, -1),
            isCharging = prefs.getBoolean(KEY_IS_CHARGING, false),
            lowPowerMode = prefs.getBoolean(KEY_LOW_POWER, false),
            syncLimited = prefs.getBoolean(KEY_SYNC_LIMITED, false),
            message = prefs.getString(KEY_MESSAGE, "").orEmpty(),
            updatedAtEpochMs = prefs.getLong(KEY_UPDATED_AT, 0L),
            manufacturer = prefs.getString(KEY_MANUFACTURER, "").orEmpty(),
            model = prefs.getString(KEY_MODEL, "").orEmpty(),
            device = prefs.getString(KEY_DEVICE, "").orEmpty(),
            appInstalled = prefs.getBoolean(KEY_APP_INSTALLED, false),
            appVersionName = prefs.getString(KEY_APP_VERSION_NAME, "").orEmpty(),
            appVersionCode = prefs.getLong(KEY_APP_VERSION_CODE, 0L),
            supportsTile = prefs.getBoolean(KEY_SUPPORTS_TILE, false),
            supportsComplication = prefs.getBoolean(KEY_SUPPORTS_COMPLICATION, false),
            ackFailureCount = prefs.getInt(KEY_ACK_FAILURE_COUNT, 0),
        ).takeIf { it.isFresh() }
    }

    companion object {
        private const val PREFS_NAME = "widget_g7_watch_health"
        private const val KEY_BATTERY_LEVEL = "battery_level"
        private const val KEY_IS_CHARGING = "is_charging"
        private const val KEY_LOW_POWER = "low_power"
        private const val KEY_SYNC_LIMITED = "sync_limited"
        private const val KEY_MESSAGE = "message"
        private const val KEY_UPDATED_AT = "updated_at"
        private const val KEY_MANUFACTURER = "manufacturer"
        private const val KEY_MODEL = "model"
        private const val KEY_DEVICE = "device"
        private const val KEY_APP_INSTALLED = "app_installed"
        private const val KEY_APP_VERSION_NAME = "app_version_name"
        private const val KEY_APP_VERSION_CODE = "app_version_code"
        private const val KEY_SUPPORTS_TILE = "supports_tile"
        private const val KEY_SUPPORTS_COMPLICATION = "supports_complication"
        private const val KEY_ACK_FAILURE_COUNT = "ack_failure_count"
    }
}
