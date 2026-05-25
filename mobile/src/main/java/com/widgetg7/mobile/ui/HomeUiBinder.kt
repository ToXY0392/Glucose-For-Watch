package com.widgetg7.mobile.ui

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.widgetg7.mobile.R

object HomeUiBinder {
    fun bind(
        syncNowButton: ImageButton,
        homeWatchFaceValue: TextView,
        homeWatchFaceMeta: TextView,
        homeConnectionStatus: TextView,
        homeBatteryStatus: TextView,
        homeBatteryIcon: View,
        homeStatusSeparator: View,
        homeSyncAgeStatus: TextView,
        homeSyncStatusLine: TextView,
        homeBatterySettingSubtitle: TextView,
        homeInstallSettingRow: View,
        dexcomRowStatus: TextView,
        watchRowStatus: TextView,
        state: HomeUiState,
    ) {
        homeWatchFaceValue.text = state.watchFaceValueText
        homeWatchFaceValue.setTextColor(state.watchFaceValueColor)
        homeWatchFaceMeta.text = state.watchFaceMetaText
        homeWatchFaceMeta.isVisible = state.watchFaceMetaVisible
        homeConnectionStatus.text = state.connectionLabel
        homeBatteryStatus.text = state.batteryLabel
        homeSyncAgeStatus.text = state.syncAgeLabel
        homeBatteryIcon.isVisible = state.showBattery
        homeBatteryStatus.isVisible = state.showBattery
        homeStatusSeparator.isVisible = state.showBattery
        homeSyncStatusLine.text = state.syncStatusLine
        homeSyncStatusLine.isVisible = state.syncStatusLineVisible
        if (state.syncStatusLineVisible) {
            val context = homeSyncStatusLine.context
            homeSyncStatusLine.setTextColor(ContextCompat.getColor(context, state.syncStatusLineTextColorRes))
            val radius = context.resources.getDimension(R.dimen.wg7_companion_pill_radius)
            homeSyncStatusLine.background =
                GradientDrawable().apply {
                    cornerRadius = radius
                    setColor(ContextCompat.getColor(context, state.syncStatusLineBackgroundColorRes))
                }
        }
        dexcomRowStatus.text = state.dexcomRowStatus
        watchRowStatus.text = state.watchRowStatus
        homeBatterySettingSubtitle.text = state.batterySettingSubtitle
        homeInstallSettingRow.isVisible = state.showInstallRow
        syncNowButton.imageTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(syncNowButton.context, state.syncButtonTintColorRes),
            )
    }
}
