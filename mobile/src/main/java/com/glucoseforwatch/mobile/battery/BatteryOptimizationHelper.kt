package com.glucoseforwatch.mobile.battery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings

/** Battery optimization exemption state shown on the watch setup screen. */
data class BatteryOptimizationStatus(
    val isProtectedFromOptimization: Boolean,
) {
    fun label(): String {
        return if (isProtectedFromOptimization) {
            "Batterie : synchronisation plus fiable active"
        } else {
            "Batterie : optimisation active, la sync peut être retardée"
        }
    }
}

/** Reads battery optimization status and builds the system settings intent. */
class BatteryOptimizationHelper(private val context: Context) {

    fun loadStatus(): BatteryOptimizationStatus {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return BatteryOptimizationStatus(powerManager.isIgnoringBatteryOptimizations(context.packageName))
    }

    fun buildSettingsIntent(): Intent {
        return Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }
}
