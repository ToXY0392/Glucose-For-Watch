package com.widgetg7.wear.sync

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.os.Build
import android.os.BatteryManager
import android.os.PowerManager
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.data.GlucoseKeys
import com.widgetg7.wear.data.WatchSyncHealthSnapshot

class WatchSyncHealthMonitor(private val context: Context) {
    private val cache = GlucoseCache(context)

    fun updateAndReport(nowEpochMs: Long = System.currentTimeMillis()): WatchSyncHealthSnapshot {
        val snapshot = evaluate(nowEpochMs)
        cache.saveWatchSyncHealth(snapshot)
        report(snapshot)
        return snapshot
    }

    private fun evaluate(nowEpochMs: Long): WatchSyncHealthSnapshot {
        val batteryLevel = currentBatteryLevel()
        val lowPowerMode = currentLowPowerMode()
        val refreshStatus = cache.loadRefreshStatusRaw()
        val glucose = cache.load()
        val refreshTimedOut =
            refreshStatus?.status == GlucoseKeys.REFRESH_IN_PROGRESS &&
                nowEpochMs - refreshStatus.updatedAtEpochMs > REFRESH_TIMEOUT_MS
        val staleAgeMs = glucose?.let { nowEpochMs - it.timestampEpochMs } ?: Long.MAX_VALUE
        val lowBattery = batteryLevel in 0..LOW_BATTERY_THRESHOLD
        val staleWhileLowBattery = lowBattery && staleAgeMs > STALE_WHILE_LOW_BATTERY_MS
        val syncLimited = (lowPowerMode || lowBattery) && (refreshTimedOut || staleWhileLowBattery)
        val message = when {
            syncLimited -> "Batterie faible, sync limitée"
            lowBattery -> "Batterie faible"
            lowPowerMode -> "Mode economie d'energie actif"
            else -> "RAS"
        }
        return WatchSyncHealthSnapshot(
            batteryLevel = batteryLevel,
            lowPowerMode = lowPowerMode,
            syncLimited = syncLimited,
            message = message,
            updatedAtEpochMs = nowEpochMs,
        )
    }

    private fun report(snapshot: WatchSyncHealthSnapshot) {
        val request = PutDataMapRequest.create(GlucoseKeys.PATH_WATCH_STATUS).apply {
            dataMap.putInt(GlucoseKeys.WATCH_BATTERY_LEVEL, snapshot.batteryLevel)
            dataMap.putBoolean(GlucoseKeys.WATCH_LOW_POWER, snapshot.lowPowerMode)
            dataMap.putBoolean(GlucoseKeys.WATCH_SYNC_LIMITED, snapshot.syncLimited)
            dataMap.putString(GlucoseKeys.WATCH_STATUS_MESSAGE, snapshot.message)
            dataMap.putLong(GlucoseKeys.WATCH_STATUS_UPDATED_AT, snapshot.updatedAtEpochMs)
            dataMap.putString(GlucoseKeys.WATCH_MANUFACTURER, Build.MANUFACTURER.orEmpty())
            dataMap.putString(GlucoseKeys.WATCH_MODEL, Build.MODEL.orEmpty())
            dataMap.putString(GlucoseKeys.WATCH_DEVICE, Build.DEVICE.orEmpty())
            dataMap.putBoolean(GlucoseKeys.WATCH_APP_INSTALLED, true)
            dataMap.putString(GlucoseKeys.WATCH_APP_VERSION_NAME, packageInfo().versionName.orEmpty())
            dataMap.putLong(GlucoseKeys.WATCH_APP_VERSION_CODE, packageVersionCode())
            dataMap.putBoolean(GlucoseKeys.WATCH_SUPPORTS_TILE, true)
            dataMap.putBoolean(GlucoseKeys.WATCH_SUPPORTS_COMPLICATION, true)
        }.asPutDataRequest().setUrgent()
        Wearable.getDataClient(context).putDataItem(request)
    }

    private fun packageInfo(): PackageInfo =
        context.packageManager.getPackageInfo(context.packageName, 0)

    private fun packageVersionCode(): Long {
        val info = packageInfo()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toLong()
        }
    }

    private fun currentBatteryLevel(): Int {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        if (level < 0 || scale <= 0) return -1
        return (level * 100) / scale
    }

    private fun currentLowPowerMode(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isPowerSaveMode
    }

    companion object {
        private const val LOW_BATTERY_THRESHOLD = 20
        private const val REFRESH_TIMEOUT_MS = 15_000L
        private const val STALE_WHILE_LOW_BATTERY_MS = 2 * 60 * 1000L
    }
}
