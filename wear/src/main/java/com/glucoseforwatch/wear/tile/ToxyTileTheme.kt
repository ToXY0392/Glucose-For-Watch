package com.glucoseforwatch.wear.tile

import androidx.annotation.Keep
import androidx.wear.protolayout.DeviceParametersBuilders
import com.glucoseforwatch.core.model.AgpGlucoseColors
import com.glucoseforwatch.core.model.GlucoseRangeResolver
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import kotlin.math.min

/**
 * Chrome tokens and AGP glucose coloring for the Wear tile.
 *
 * Layout metrics adapt padding/typography to [screenWidthDp] and [screenShape].
 * Sync button and glucose/trend slots use **fixed** dp sizes so data changes
 * (value digits, trend presence, sync lock) do not shift the ProtoLayout.
 *
 * Medical value colors come only from [AgpGlucoseColors] / [GlucoseRangeResolver]
 * — never Material primary / blue chrome.
 */
@Keep
object ToxyTileTheme {
    /** Bump on every ProtoLayout / resource-map change to bust Wear tile resource cache. */
    const val RESOURCES_VERSION = "simple-tile-v22-GlucoseTileServiceV2-zero-ghost"
    const val FRESHNESS_INTERVAL_MS = 45_000L
    const val COMPLICATION_REFRESH_INTERVAL_MS = 15_000L
    /** Clickable id — bumped with V2 provider to avoid stale click routing. */
    const val SYNC_CLICK_ID = "sync_v2"

    const val BACKGROUND = 0xFF0F1419.toInt()
    const val UNIT_TEXT = 0xFF9CA3AF.toInt()
    /** Neutral chrome for sync label — blue is banned on medical surfaces. */
    const val SYNC_ACCENT = 0xFFD1D5DB.toInt()
    const val SYNC_BG = 0x33D1D5DB.toInt()
    const val SYNC_LOCKED_ACCENT = 0xFF6B7280.toInt()
    const val SYNC_LOCKED_BG = 0x226B7280.toInt()
    /** Fully transparent — trend placeholder when [showTrend] is false. */
    const val TRANSPARENT = 0x00000000

    /** Legacy pill metrics (unused by minimal sync text; kept for tests/compat). */
    const val SYNC_BUTTON_WIDTH_DP = 120f
    const val SYNC_BUTTON_HEIGHT_DP = 40f
    const val SYNC_TEXT_SP = 13f
    const val SYNC_TEXT_SLOT_HEIGHT_DP = 28f

    /** Fixed glucose value slot — sized for three monospace digits. */
    const val VALUE_ROW_WIDTH_DP = 120f

    private const val COMPACT_SCREEN_WIDTH_DP = 225

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
        val valueRowWidthDp: Float,
        val valueRowHeightDp: Float,
        val trendGapDp: Float,
        val trendSlotWidthDp: Float,
        val trendSlotHeightDp: Float,
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
        val trendSp = if (compact) 20f else 24f
        return TileLayoutMetrics(
            horizontalPadDp = inset,
            topPadDp = if (round) 8f else if (compact) 6f else 4f,
            bottomPadDp = if (round) 14f else if (compact) 10f else 8f,
            valueSp = if (compact) 38f else 44f,
            unitSp = if (compact) 13f else 15f,
            trendSp = trendSp,
            valueMetaGapDp = if (compact) 2f else 4f,
            metaSyncGapDp = if (compact) 10f else 12f,
            syncButtonHeightDp = SYNC_BUTTON_HEIGHT_DP,
            syncButtonWidthDp = SYNC_BUTTON_WIDTH_DP,
            valueRowWidthDp = VALUE_ROW_WIDTH_DP,
            valueRowHeightDp = if (compact) 44f else 48f,
            trendGapDp = if (compact) 8f else 10f,
            trendSlotWidthDp = if (compact) 22f else 26f,
            trendSlotHeightDp = trendSp + 4f,
        )
    }

    fun valueSpForText(baseSp: Float, valueText: String): Float =
        when {
            valueText.length >= 3 && baseSp >= 44f -> baseSp - 6f
            valueText.length >= 3 && baseSp >= 38f -> baseSp - 4f
            else -> baseSp
        }

    /** Glycemic value color — AGP only, never theme primary. */
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
