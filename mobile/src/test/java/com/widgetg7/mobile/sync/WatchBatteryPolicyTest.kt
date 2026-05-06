package com.widgetg7.mobile.sync

import com.widgetg7.mobile.watch.WatchSyncHealthStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class WatchBatteryPolicyTest {
    @Test
    fun matrix_degraded_state_matches_battery_and_charging_rules() {
        val levels = listOf(25, 20, 15, 10)
        val chargingStates = listOf(false, true)

        for (level in levels) {
            for (charging in chargingStates) {
                val health = health(level = level, isCharging = charging, syncLimited = false)
                val expected = level <= 20 && !charging
                assertEquals("level=$level charging=$charging", expected, WatchBatteryPolicy.isDegraded(health))
            }
        }
    }

    @Test
    fun sync_limited_forces_degraded_even_if_charging() {
        val health = health(level = 25, isCharging = true, syncLimited = true)
        assertEquals(true, WatchBatteryPolicy.isDegraded(health))
    }

    @Test
    fun poll_interval_matches_degraded_mode() {
        val normal = health(level = 25, isCharging = false, syncLimited = false)
        val degraded = health(level = 15, isCharging = false, syncLimited = false)

        assertEquals(WatchBatteryPolicy.POLL_INTERVAL_NORMAL_MS, WatchBatteryPolicy.pollIntervalMs(normal))
        assertEquals(WatchBatteryPolicy.POLL_INTERVAL_DEGRADED_MS, WatchBatteryPolicy.pollIntervalMs(degraded))
    }

    private fun health(
        level: Int,
        isCharging: Boolean,
        syncLimited: Boolean,
    ): WatchSyncHealthStatus =
        WatchSyncHealthStatus(
            batteryLevel = level,
            isCharging = isCharging,
            lowPowerMode = false,
            syncLimited = syncLimited,
            message = "",
            updatedAtEpochMs = System.currentTimeMillis(),
            manufacturer = "",
            model = "",
            device = "",
            appInstalled = true,
            appVersionName = "0.0.0",
            appVersionCode = 1L,
            supportsTile = true,
            supportsComplication = true,
        )
}
