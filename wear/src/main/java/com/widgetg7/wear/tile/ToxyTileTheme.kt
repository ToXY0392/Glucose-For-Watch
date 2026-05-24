package com.widgetg7.wear.tile

import androidx.wear.protolayout.DeviceParametersBuilders
import com.widgetg7.core.model.AgpGlucoseColors
import com.widgetg7.core.model.GlucoseRangeResolver
import com.widgetg7.wear.data.GlucoseSnapshot
import kotlin.math.min

/**
 * Chrome tokens and AGP glucose coloring for the Wear tile.
 *
 * Layout metrics adapt to [screenWidthDp] and [screenShape] so content stays inside round safe zones.
 */
object ToxyTileTheme {
    const val RESOURCES_VERSION = "simple-tile-v12-sync-lock"
    const val FRESHNESS_INTERVAL_MS = 45_000L
    const val COMPLICATION_REFRESH_INTERVAL_MS = 15_000L
    const val SYNC_CLICK_ID = "sync"

    const val BACKGROUND = 0xFF0F1419.toInt()
    const val UNIT_TEXT = 0xFF9CA3AF.toInt()
    const val SYNC_ACCENT = 0xFF6EB5FF.toInt()
    const val SYNC_BG = 0x336EB5FF.toInt()
    const val SYNC_LOCKED_ACCENT = 0xFF6B7280.toInt()
    const val SYNC_LOCKED_BG = 0x226B7280.toInt()

    private const val COMPACT_SCREEN_WIDTH_DP = 225
    private const val MIN_SYNC_BUTTON_WIDTH_DP = 112f
    private const val MAX_SYNC_BUTTON_WIDTH_DP = 140f

    data class TileLayoutMetrics(
        val horizontalPadDp: Float,
        val topPadDp: Float,
        val bottomPadDp: Float,
        val valueSp: Float,
        val unitSp: Float,
        val trendSp: Float,
        val valueMetaGapDp: Float,
        val metaSyncGapDp: Float,
        val syncButtonHeightDp: Float,
        val syncButtonWidthDp: Float,
        val valueRowHeightDp: Float,
        val trendGapDp: Float,
    )

    fun layoutMetrics(
        screenWidthDp: Int,
        screenHeightDp: Int,
        screenShape: Int = DeviceParametersBuilders.SCREEN_SHAPE_UNDEFINED,
    ): TileLayoutMetrics {
        val round =
            screenShape == DeviceParametersBuilders.SCREEN_SHAPE_ROUND ||
                (
                    screenShape == DeviceParametersBuilders.SCREEN_SHAPE_UNDEFINED &&
                        min(screenWidthDp, screenHeightDp) <= COMPACT_SCREEN_WIDTH_DP
                )
        val compact = min(screenWidthDp, screenHeightDp) < COMPACT_SCREEN_WIDTH_DP
        val inset =
            when {
                round -> 26f
                compact -> 20f
                else -> 14f
            }
        val syncButtonWidth =
            (screenWidthDp - inset * 2f - if (round) 12f else 4f)
                .coerceIn(MIN_SYNC_BUTTON_WIDTH_DP, MAX_SYNC_BUTTON_WIDTH_DP)
        return TileLayoutMetrics(
            horizontalPadDp = inset,
            topPadDp = if (round) 12f else if (compact) 10f else 8f,
            bottomPadDp = if (round) 22f else if (compact) 12f else 8f,
            valueSp = if (compact) 38f else 44f,
            unitSp = if (compact) 13f else 15f,
            trendSp = if (compact) 20f else 24f,
            valueMetaGapDp = if (compact) 2f else 4f,
            metaSyncGapDp = if (compact) 10f else 12f,
            syncButtonHeightDp = 40f,
            syncButtonWidthDp = syncButtonWidth,
            valueRowHeightDp = if (compact) 44f else 48f,
            trendGapDp = if (compact) 8f else 10f,
        )
    }

    fun valueSpForText(baseSp: Float, valueText: String): Float =
        when {
            valueText.length >= 3 && baseSp >= 44f -> baseSp - 6f
            valueText.length >= 3 && baseSp >= 38f -> baseSp - 4f
            else -> baseSp
        }

    fun valueColorArgb(snapshot: GlucoseSnapshot?): Int =
        if (snapshot == null) {
            AgpGlucoseColors.UNKNOWN
        } else {
            GlucoseRangeResolver.resolveColorForReading(snapshot.valueMgDl, snapshot.stale)
        }

    fun trendColorArgb(snapshot: GlucoseSnapshot?): Int =
        when {
            snapshot == null || snapshot.stale -> AgpGlucoseColors.UNKNOWN
            else -> GlucoseRangeResolver.resolveColor(snapshot.valueMgDl)
        }
}
