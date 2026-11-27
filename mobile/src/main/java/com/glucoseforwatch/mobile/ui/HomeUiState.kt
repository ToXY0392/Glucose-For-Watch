package com.glucoseforwatch.mobile.ui

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.glucoseforwatch.core.model.GlucoseDisplayUnit

/** Precomputed fields bound to the home screen layout. */
data class HomeUiState(
    val watchFaceValueText: String,
    val watchFaceValueMgDl: Int?,
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
    val unitRowStatus: String,
    val watchRowStatus: String,
    val batterySettingSubtitle: String,
    val showInstallRow: Boolean,
    @ColorRes val syncButtonTintColorRes: Int,
    /** Reading older than GlucoseFreshness.STALE_AFTER_MS: drawn in the AGP unknown color. */
    val heroStale: Boolean = false,
    /** Dexcom trend token (UP, UP_RIGHT, FLAT, DOWN_RIGHT, DOWN, NONE). NONE draws no arrow. */
    val heroTrend: String = "NONE",
    val heroTrendLabel: String = "",
    val heroDisplayUnit: GlucoseDisplayUnit = GlucoseDisplayUnit.MG_DL,
    val watchConnected: Boolean = false,
    /** 0..100 when the connected watch reported its battery, else null. */
    val watchBatteryPercent: Int? = null,
) {
    /** Hero glucose value shown on the watch-face preview (alias for layout handoff). */
    val heroGlucoseValue: String get() = watchFaceValueText

    /** Raw mg/dL used for dial color bands and value slide direction. */
    val heroGlucoseValueMgDl: Int? get() = watchFaceValueMgDl
}
