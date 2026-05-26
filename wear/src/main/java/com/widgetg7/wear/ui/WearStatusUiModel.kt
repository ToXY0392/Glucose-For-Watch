package com.widgetg7.wear.ui

import android.content.Context
import com.widgetg7.wear.R
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.data.GlucoseKeys
import com.widgetg7.wear.display.WearGlucoseSurfaceModelFactory
import com.widgetg7.wear.sync.WatchSyncHealthMonitor

/** View state for the Wear status screen (tile + sync chrome). */
data class WearStatusUiModel(
    val valueText: String,
    val unitText: String,
    val trendArrow: String,
    val showTrend: Boolean,
    val valueColorArgb: Int,
    val trendColorArgb: Int,
    val stale: Boolean,
    val syncStatusText: String,
    val syncStatusIsError: Boolean,
    val batteryLine: String?,
    val healthMessage: String?,
) {
    val hasReading: Boolean = valueText != "--"
}

/** Builds [WearStatusUiModel] from cache, refresh status, and watch health. */
object WearStatusUiModelFactory {
    fun load(context: Context, nowEpochMs: Long = System.currentTimeMillis()): WearStatusUiModel {
        val cache = GlucoseCache(context)
        val health = WatchSyncHealthMonitor(context).updateAndReport(nowEpochMs)
        val display = WearGlucoseSurfaceModelFactory.fromSnapshot(cache.load())
        val refreshStatus = cache.loadRefreshStatus(nowEpochMs)

        val syncStatusText =
            when {
                refreshStatus != null -> refreshStatus.displayText(nowEpochMs)
                display.stale && display.valueText != "--" ->
                    context.getString(R.string.wear_status_stale)
                display.valueText != "--" -> context.getString(R.string.wear_status_refresh_ok)
                else -> context.getString(R.string.wear_status_phone_hint)
            }
        val syncStatusIsError =
            refreshStatus?.status == GlucoseKeys.REFRESH_FAILED ||
                (display.stale && display.valueText != "--")

        val batteryLine =
            if (health.batteryLevel in 0..100) {
                context.getString(R.string.wear_status_battery, health.batteryLevel)
            } else {
                null
            }
        val healthMessage =
            health.message.takeIf { it.isNotBlank() && it != "RAS" }

        return WearStatusUiModel(
            valueText = display.valueText,
            unitText = context.getString(R.string.wear_status_unit),
            trendArrow = display.trendArrow,
            showTrend = display.showTrend,
            valueColorArgb = display.valueColorArgb,
            trendColorArgb = display.trendColorArgb,
            stale = display.stale,
            syncStatusText = syncStatusText,
            syncStatusIsError = syncStatusIsError,
            batteryLine = batteryLine,
            healthMessage = healthMessage,
        )
    }
}
