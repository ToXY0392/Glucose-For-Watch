package com.widgetg7.mobile.ui

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

data class HomeUiState(
    val watchFaceValueText: String,
    @ColorInt val watchFaceValueColor: Int,
    val watchFaceMetaText: String,
    val watchFaceMetaVisible: Boolean,
    val connectionLabel: String,
    val batteryLabel: String,
    val syncAgeLabel: String,
    val showBattery: Boolean,
    val syncStatusLine: String,
    val syncStatusLineVisible: Boolean,
    @ColorRes val syncStatusLineTextColorRes: Int,
    @ColorRes val syncStatusLineBackgroundColorRes: Int,
    val dexcomRowStatus: String,
    val watchRowStatus: String,
    val batterySettingSubtitle: String,
    val showInstallRow: Boolean,
    @ColorRes val syncButtonTintColorRes: Int,
)
