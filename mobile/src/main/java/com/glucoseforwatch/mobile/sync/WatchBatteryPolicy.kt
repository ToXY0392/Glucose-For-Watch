package com.glucoseforwatch.mobile.sync

import com.glucoseforwatch.feature.sync.SyncMessageCatalog
import com.glucoseforwatch.mobile.watch.WatchSyncHealthStatus
import com.glucoseforwatch.feature.sync.BatteryDegradedPolicy

/** Poll interval and messaging when watch battery is degraded. */
object WatchBatteryPolicy {
    const val POLL_INTERVAL_NORMAL_MS = BatteryDegradedPolicy.POLL_INTERVAL_NORMAL_MS
    const val POLL_INTERVAL_DEGRADED_MS = BatteryDegradedPolicy.POLL_INTERVAL_DEGRADED_MS

    fun isDegraded(health: WatchSyncHealthStatus?): Boolean {
        if (health == null) return false
        return BatteryDegradedPolicy.isDegraded(
            batteryLevel = health.batteryLevel,
            isCharging = health.isCharging,
            syncLimited = health.syncLimited,
        )
    }

    fun pollIntervalMs(health: WatchSyncHealthStatus?): Long =
        if (health == null) {
            POLL_INTERVAL_NORMAL_MS
        } else {
            BatteryDegradedPolicy.pollIntervalMs(
                batteryLevel = health.batteryLevel,
                isCharging = health.isCharging,
                syncLimited = health.syncLimited,
            )
        }

    fun withDegradedSuffix(base: String, health: WatchSyncHealthStatus?): String =
        if (isDegraded(health)) {
            "$base ${SyncMessageCatalog.DEGRADED_SUFFIX}"
        } else {
            base
        }
}
