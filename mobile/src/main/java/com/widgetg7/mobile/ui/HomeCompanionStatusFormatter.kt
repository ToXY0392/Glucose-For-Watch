package com.widgetg7.mobile.ui

import android.content.Context
import com.widgetg7.core.model.SyncStatusSnapshot
import com.widgetg7.feature.sync.SyncReadingTextFormatter
import com.widgetg7.mobile.R
import com.widgetg7.mobile.watch.WatchConnectionStatus
import com.widgetg7.mobile.watch.WatchSyncHealthStatus

/** Formats connection, battery, and sync-age strip under the watch face. */
object HomeCompanionStatusFormatter {
    /** Localized labels for the companion status strip. */
    data class StatusLabels(
        val connected: String,
        val disconnected: String,
        val batteryPercent: (Int) -> String,
        val batteryUnknown: String,
        val syncNow: String,
        val syncNever: String,
    )

    /** Formatted companion strip fields for the home screen. */
    data class CompanionStatus(
        val connectionLabel: String,
        val batteryLabel: String,
        val syncAgeLabel: String,
        val showBattery: Boolean,
    )

    fun format(
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
        syncStatus: SyncStatusSnapshot,
        labels: StatusLabels,
        nowEpochMs: Long = System.currentTimeMillis(),
    ): CompanionStatus {
        val connectionLabel =
            if (watchStatus.connected) {
                labels.connected
            } else {
                labels.disconnected
            }

        val batteryLevel = watchHealth?.batteryLevel ?: -1
        val batteryLabel =
            if (watchStatus.connected && batteryLevel in 0..100) {
                labels.batteryPercent(batteryLevel)
            } else {
                labels.batteryUnknown
            }

        val syncEpochMs = HomeReadingTimeResolver.displayEpochMs(syncStatus)
        val syncAgeLabel =
            when {
                syncEpochMs <= 0L -> labels.syncNever
                nowEpochMs - syncEpochMs < 60_000L -> labels.syncNow
                else ->
                    SyncReadingTextFormatter.readingAgeLabel(
                        readingEpochMs = syncEpochMs,
                        nowEpochMs = nowEpochMs,
                    )
            }

        return CompanionStatus(
            connectionLabel = connectionLabel,
            batteryLabel = batteryLabel,
            syncAgeLabel = syncAgeLabel,
            showBattery = watchStatus.connected,
        )
    }

    fun defaultLabels(context: Context): StatusLabels =
        StatusLabels(
            connected = context.getString(R.string.home_companion_connected),
            disconnected = context.getString(R.string.home_companion_disconnected),
            batteryPercent = { level -> context.getString(R.string.home_companion_battery, level) },
            batteryUnknown = context.getString(R.string.home_companion_battery_unknown),
            syncNow = context.getString(R.string.home_companion_sync_now),
            syncNever = context.getString(R.string.home_companion_sync_never),
        )
}
