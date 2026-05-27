package com.glucoseforwatch.feature.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class BatteryDegradedPolicyTest {
    @Test
    fun degraded_matrix_matches_battery_and_charging_rules() {
        val levels = listOf(25, 20, 15, 10)
        val chargingStates = listOf(false, true)

        for (level in levels) {
            for (charging in chargingStates) {
                val expected = level <= 20 && !charging
                assertEquals(
                    "level=$level charging=$charging",
                    expected,
                    BatteryDegradedPolicy.isDegraded(
                        batteryLevel = level,
                        isCharging = charging,
                        syncLimited = false,
                    ),
                )
            }
        }
    }

    @Test
    fun sync_limited_forces_degraded_even_when_charging() {
        assertEquals(
            true,
            BatteryDegradedPolicy.isDegraded(
                batteryLevel = 40,
                isCharging = true,
                syncLimited = true,
            ),
        )
    }

    @Test
    fun poll_interval_matches_degraded_state() {
        assertEquals(
            BatteryDegradedPolicy.POLL_INTERVAL_NORMAL_MS,
            BatteryDegradedPolicy.pollIntervalMs(
                batteryLevel = 25,
                isCharging = false,
                syncLimited = false,
            ),
        )
        assertEquals(
            BatteryDegradedPolicy.POLL_INTERVAL_DEGRADED_MS,
            BatteryDegradedPolicy.pollIntervalMs(
                batteryLevel = 10,
                isCharging = false,
                syncLimited = false,
            ),
        )
    }
}
