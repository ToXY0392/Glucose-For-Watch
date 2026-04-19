package com.widgetg7.mobile.battery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

data class BatteryOptimizationStatus(
    val isProtectedFromOptimization: Boolean,
) {
    fun label(): String {
        return if (isProtectedFromOptimization) {
            "Batterie: synchronisation plus fiable active"
        } else {
            "Batterie: optimisation active, la sync peut etre retardee"
        }
    }
}

class BatteryOptimizationHelper(private val context: Context) {

    fun loadStatus(): BatteryOptimizationStatus {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val ignored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
        return BatteryOptimizationStatus(ignored)
    }

    fun buildSettingsIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        } else {
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        }
    }
}
