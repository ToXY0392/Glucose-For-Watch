package com.widgetg7.mobile.ui

import android.content.Context
import com.widgetg7.core.model.GlucoseDisplayUnit
import com.widgetg7.core.model.GlucoseRangeResolver
import com.widgetg7.core.model.SyncStatusSnapshot
import com.widgetg7.feature.sync.SyncReadingTextFormatter
import com.widgetg7.feature.sync.SyncTrendTextFormatter
import com.widgetg7.mobile.R
import com.widgetg7.mobile.settings.DexcomUserSettings
import com.widgetg7.mobile.sync.PhoneSyncStateSnapshot
import com.widgetg7.mobile.watch.WatchConnectionStatus
import com.widgetg7.mobile.watch.WatchHomeCardSummary
import com.widgetg7.mobile.watch.WatchSyncHealthStatus
import androidx.core.content.ContextCompat

/** Maps repositories and settings into [HomeUiState]. */
object HomeStateMapper {
    fun map(
        context: Context,
        dexcomSettings: DexcomUserSettings,
        syncStatus: SyncStatusSnapshot,
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
        syncState: PhoneSyncStateSnapshot,
        batteryProtected: Boolean,
        watchPushPending: Boolean,
        activeSyncEnabled: Boolean,
        displayUnit: GlucoseDisplayUnit,
        nowEpochMs: Long = System.currentTimeMillis(),
    ): HomeUiState {
        val dexcomConfigured = dexcomSettings.isConfigured()
        val pillLabels = HomeSyncPillLabelsFactory.create(context)
        val watchReady = watchHealth?.appInstalled == true
        val syncStatusLine =
            HomeSyncPillResolver.resolve(
                dexcomConfigured = dexcomConfigured,
                activeSync = activeSyncEnabled,
                syncStatus = syncStatus,
                watchStatus = watchStatus,
                watchReady = watchReady,
                syncState = syncState,
                watchPushPending = watchPushPending,
                labels = pillLabels,
            )
        val syncStatusTone =
            HomeSyncPillResolver.resolveTone(
                dexcomConfigured = dexcomConfigured,
                activeSync = activeSyncEnabled,
                syncStatus = syncStatus,
                watchStatus = watchStatus,
                watchReady = watchReady,
                syncState = syncState,
                watchPushPending = watchPushPending,
            )
        val syncStatusColors = HomeSyncPillToneColors.forTone(syncStatusTone)
        val companionLabels = HomeCompanionStatusFormatter.defaultLabels(context)
        val companionStatus =
            HomeCompanionStatusFormatter.format(
                watchStatus = watchStatus,
                watchHealth = watchHealth,
                syncStatus = syncStatus,
                labels = companionLabels,
                nowEpochMs = nowEpochMs,
            )
        val watchRow =
            WatchHomeCardSummary.resolve(
                context = context,
                watchStatus = watchStatus,
                watchHealth = watchHealth,
                syncState = syncState,
            )
        val watchFace = resolveWatchFace(context, dexcomConfigured, syncStatus, displayUnit, nowEpochMs)
        val syncTint =
            when {
                !dexcomConfigured || syncStatus.lastError.isNotBlank() -> R.color.wg7_text_tertiary
                watchPushPending -> R.color.wg7_accent_dark
                else -> R.color.wg7_icon_tint
            }
        val needsInstall =
            watchStatus.connected &&
                (watchHealth == null || !watchHealth.appInstalled)

        return HomeUiState(
            watchFaceValueText = watchFace.valueText,
            watchFaceValueColor = watchFace.valueColor,
            watchFaceMetaText = watchFace.metaText,
            watchFaceMetaVisible = watchFace.metaVisible,
            connectionLabel = companionStatus.connectionLabel,
            batteryLabel = companionStatus.batteryLabel,
            syncAgeLabel = companionStatus.syncAgeLabel,
            showBattery = companionStatus.showBattery,
            syncStatusLine = syncStatusLine,
            syncStatusLineVisible = dexcomConfigured,
            syncStatusLineTextColorRes = syncStatusColors.textColorRes,
            syncStatusLineBackgroundColorRes = syncStatusColors.backgroundColorRes,
            dexcomRowStatus = resolveDexcomRowStatus(context, dexcomSettings),
            unitRowStatus = GlucoseDisplayFormatter.unitLabel(displayUnit),
            watchRowStatus = watchRow.subtitle,
            batterySettingSubtitle =
                if (batteryProtected) {
                    context.getString(R.string.home_companion_setting_battery_on)
                } else {
                    context.getString(R.string.home_companion_setting_battery_off)
                },
            showInstallRow = needsInstall,
            syncButtonTintColorRes = syncTint,
        )
    }

    private data class WatchFaceFields(
        val valueText: String,
        val valueColor: Int,
        val metaText: String,
        val metaVisible: Boolean,
    )

    private fun resolveWatchFace(
        context: Context,
        dexcomConfigured: Boolean,
        syncStatus: SyncStatusSnapshot,
        displayUnit: GlucoseDisplayUnit,
        nowEpochMs: Long,
    ): WatchFaceFields {
        val metaColor = ContextCompat.getColor(context, R.color.wg7_watch_face_meta)
        val hasReading = syncStatus.lastValueMgDl != null
        return when {
            hasReading -> {
                val value = syncStatus.lastValueMgDl!!
                WatchFaceFields(
                    valueText = GlucoseDisplayFormatter.formatValue(value, displayUnit),
                    valueColor = GlucoseRangeResolver.resolveColor(value),
                    metaText = buildWatchFaceMeta(context, syncStatus, displayUnit, nowEpochMs),
                    metaVisible = true,
                )
            }
            !dexcomConfigured ->
                WatchFaceFields(
                    valueText = context.getString(R.string.home_companion_watch_face_empty),
                    valueColor = metaColor,
                    metaText = context.getString(R.string.home_hero_connect),
                    metaVisible = true,
                )
            else ->
                WatchFaceFields(
                    valueText = context.getString(R.string.home_companion_watch_face_empty),
                    valueColor = metaColor,
                    metaText = context.getString(R.string.home_hero_waiting),
                    metaVisible = true,
                )
        }
    }

    private fun buildWatchFaceMeta(
        context: Context,
        syncStatus: SyncStatusSnapshot,
        displayUnit: GlucoseDisplayUnit,
        nowEpochMs: Long,
    ): String {
        val trendLabel = SyncTrendTextFormatter.displayTrend(syncStatus.lastTrend).trim()
        val readingEpochMs = HomeReadingTimeResolver.displayEpochMs(syncStatus)
        val ageLabel =
            if (readingEpochMs > 0L) {
                SyncReadingTextFormatter.readingAgeLabel(
                    readingEpochMs = readingEpochMs,
                    nowEpochMs = nowEpochMs,
                )
            } else {
                ""
            }
        val parts = mutableListOf<String>()
        if (trendLabel.isNotBlank()) parts += trendLabel
        if (ageLabel.isNotBlank()) parts += ageLabel
        val suffix = parts.joinToString(" · ")
        val unitLabel = GlucoseDisplayFormatter.unitLabel(displayUnit)
        return if (suffix.isBlank()) {
            unitLabel
        } else {
            "$unitLabel · $suffix"
        }
    }

    private fun resolveDexcomRowStatus(
        context: Context,
        settings: DexcomUserSettings,
    ): String =
        if (settings.isConfigured()) {
            val username = settings.username.trim()
            if (username.isNotEmpty()) {
                "${context.getString(R.string.home_dexcom_status_on)} · ${context.getString(R.string.home_dexcom_user_mask, username.take(3))}"
            } else {
                context.getString(R.string.home_dexcom_status_on)
            }
        } else {
            context.getString(R.string.home_dexcom_status_off)
        }
}
