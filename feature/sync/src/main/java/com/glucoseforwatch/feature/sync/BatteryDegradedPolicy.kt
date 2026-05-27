package com.glucoseforwatch.feature.sync

/**
 * Low-battery watch sync policy: slower poll interval below 20% when not charging.
 *
 * [syncLimited] from the watch forces degraded mode regardless of battery level.
 */
object BatteryDegradedPolicy {
    private const val LOW_BATTERY_THRESHOLD = 20
    const val POLL_INTERVAL_NORMAL_MS = 45_000L
    const val POLL_INTERVAL_DEGRADED_MS = 120_000L

    fun isDegraded(
        batteryLevel: Int,
        isCharging: Boolean,
        syncLimited: Boolean,
    ): Boolean {
        if (syncLimited) return true
        if (isCharging) return false
        return batteryLevel in 0..LOW_BATTERY_THRESHOLD
    }

    fun pollIntervalMs(
        batteryLevel: Int,
        isCharging: Boolean,
        syncLimited: Boolean,
    ): Long = if (isDegraded(batteryLevel, isCharging, syncLimited)) {
        POLL_INTERVAL_DEGRADED_MS
    } else {
        POLL_INTERVAL_NORMAL_MS
    }
}
