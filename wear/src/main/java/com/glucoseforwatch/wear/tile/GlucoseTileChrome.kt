package com.glucoseforwatch.wear.tile

import androidx.annotation.Keep
import androidx.wear.protolayout.DeviceParametersBuilders
import com.glucoseforwatch.core.model.AgpGlucoseColors
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.core.model.GlucoseRange
import com.glucoseforwatch.core.model.GlucoseRangeResolver
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Tile-only chrome and medical presentation — independent from the Toxy UX kit.
 *
 * Colors are explicit ARGB (never Material / system defaults):
 * green = target, orange alert = moderate/severe hyper (incl. 210), red = urgency.
 * Stale (>15 min): same layout tree, value/unit/trend at ~50% alpha, bottom age label.
 */
@Keep
object GlucoseTileChrome {
    /** Bump on every ProtoLayout / resource-map change to bust Wear tile resource cache. */
    const val RESOURCES_VERSION = "simple-tile-v24-orange-hyper-stale-fade"
    const val FRESHNESS_INTERVAL_MS = 45_000L
    const val SYNC_CLICK_ID = "sync_v2"

    /** Absolute black tile canvas. */
    const val BACKGROUND = 0xFF000000.toInt()
    /** Unit / chrome labels — explicit gray, not theme-derived. */
    const val UNIT_TEXT = 0xFF9CA3AF.toInt()
    const val STATUS_TEXT = 0xFFD1D5DB.toInt()
    const val STATUS_LOCKED_TEXT = 0xFF6B7280.toInt()
    /** Alert orange for moderate hyper (181–250) including 210 mg/dL. */
    const val ALERT_ORANGE = AgpGlucoseColors.VERY_HIGH
    /** ~50% alpha when reading age exceeds [STALE_AFTER_MS]. */
    const val STALE_OPACITY = 0.5f

    const val SYNC_TEXT_SP = 13f
    const val STATUS_SLOT_HEIGHT_DP = 28f
    const val VALUE_ROW_WIDTH_DP = 120f
    const val SYNC_BUTTON_WIDTH_DP = 120f
    const val SYNC_BUTTON_HEIGHT_DP = 40f

    /** Tile UI stale threshold (timestamp vs system clock). */
    const val STALE_AFTER_MS = 15 * 60 * 1000L

    private const val COMPACT_SCREEN_WIDTH_DP = 225
    private const val MISSING_VALUE = "---"
    private const val EMPTY_TREND = ""

    data class TileLayoutMetrics(
        val horizontalPadDp: Float,
        val topPadDp: Float,
        val bottomPadDp: Float,
        val valueSp: Float,
        val unitSp: Float,
        val trendSp: Float,
        val valueMetaGapDp: Float,
        val syncButtonHeightDp: Float,
        val syncButtonWidthDp: Float,
        val valueRowWidthDp: Float,
        val valueRowHeightDp: Float,
        val trendGapDp: Float,
        val trendSlotWidthDp: Float,
        val trendSlotHeightDp: Float,
    )

    /** Resolved strings/ARGB for one tile frame — layout tree stays identical every time. */
    data class TilePresentation(
        val valueText: String,
        val unitLabel: String,
        val trendText: String,
        val valueArgb: Int,
        val trendArgb: Int,
        val unitArgb: Int,
        val statusText: String,
        val statusArgb: Int,
        val stale: Boolean,
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
        // Fixed valueSp for 3 monospace digits — never shrink by digit count (avoids reflow).
        val valueSp = if (compact) 38f else 44f
        val trendSp = if (compact) 20f else 24f
        return TileLayoutMetrics(
            horizontalPadDp = inset,
            topPadDp = if (round) 8f else if (compact) 6f else 4f,
            bottomPadDp = if (round) 14f else if (compact) 10f else 8f,
            valueSp = valueSp,
            unitSp = if (compact) 13f else 15f,
            trendSp = trendSp,
            valueMetaGapDp = if (compact) 2f else 4f,
            syncButtonHeightDp = SYNC_BUTTON_HEIGHT_DP,
            syncButtonWidthDp = SYNC_BUTTON_WIDTH_DP,
            valueRowWidthDp = VALUE_ROW_WIDTH_DP,
            valueRowHeightDp = if (compact) 44f else 48f,
            trendGapDp = if (compact) 8f else 10f,
            trendSlotWidthDp = if (compact) 22f else 26f,
            trendSlotHeightDp = trendSp + 4f,
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun presentation(
        snapshot: GlucoseSnapshot?,
        syncLocked: Boolean,
        staleStatusLabel: String,
        syncActionLabel: String,
        syncInProgressLabel: String,
        nowEpochMs: Long = System.currentTimeMillis(),
    ): TilePresentation {
        if (snapshot == null) {
            return TilePresentation(
                valueText = MISSING_VALUE,
                unitLabel = GlucoseDisplayUnit.MG_DL.label(),
                trendText = EMPTY_TREND,
                valueArgb = opaqueArgb(AgpGlucoseColors.UNKNOWN),
                trendArgb = opaqueArgb(BACKGROUND),
                unitArgb = opaqueArgb(UNIT_TEXT),
                statusText = if (syncLocked) syncInProgressLabel else syncActionLabel,
                statusArgb = opaqueArgb(if (syncLocked) STATUS_LOCKED_TEXT else STATUS_TEXT),
                stale = false,
            )
        }

        val stale = isStale(snapshot.timestampEpochMs, nowEpochMs)
        val medicalArgb = opaqueArgb(medicalColorArgb(snapshot.valueMgDl))
        val trendText = snapshot.trendArrow().ifBlank { EMPTY_TREND }
        val valueArgb = if (stale) withOpacity(medicalArgb, STALE_OPACITY) else medicalArgb
        val unitArgb =
            if (stale) withOpacity(opaqueArgb(UNIT_TEXT), STALE_OPACITY) else opaqueArgb(UNIT_TEXT)
        val trendArgb =
            when {
                trendText.isEmpty() -> opaqueArgb(BACKGROUND)
                stale -> withOpacity(medicalArgb, STALE_OPACITY)
                else -> medicalArgb
            }
        // staleStatusLabel kept for call-site compat; stale uses [ageStatusLabel].
        val statusText =
            when {
                stale -> ageStatusLabel(snapshot.timestampEpochMs, nowEpochMs)
                syncLocked -> syncInProgressLabel
                else -> syncActionLabel
            }
        val statusArgb =
            opaqueArgb(
                when {
                    stale -> STATUS_TEXT
                    syncLocked -> STATUS_LOCKED_TEXT
                    else -> STATUS_TEXT
                },
            )

        return TilePresentation(
            valueText = snapshot.displayValueText().ifBlank { MISSING_VALUE },
            unitLabel = snapshot.unitLabel(),
            trendText = trendText,
            valueArgb = valueArgb,
            trendArgb = trendArgb,
            unitArgb = unitArgb,
            statusText = statusText,
            statusArgb = statusArgb,
            stale = stale,
        )
    }

    fun isStale(timestampEpochMs: Long, nowEpochMs: Long): Boolean {
        if (timestampEpochMs <= 0L) return true
        return (nowEpochMs - timestampEpochMs) > STALE_AFTER_MS
    }

    /** Neutral bottom label: "Il y a N min". */
    fun ageStatusLabel(timestampEpochMs: Long, nowEpochMs: Long): String {
        val ageMinutes = ((nowEpochMs - timestampEpochMs).coerceAtLeast(0L) / 60_000L)
        return when {
            ageMinutes <= 0L -> "À l'instant"
            ageMinutes == 1L -> "Il y a 1 min"
            else -> "Il y a $ageMinutes min"
        }
    }

    /**
     * Tile medical palette:
     * green = target · orange alert = hyper ≥181 (incl. 210) · red = hypo.
     * Core [AgpGlucoseColors.HIGH] yellow is not used on the tile for 181–250.
     */
    fun medicalColorArgb(valueMgDl: Int): Int =
        when (GlucoseRangeResolver.resolveRange(valueMgDl)) {
            GlucoseRange.IN_RANGE -> AgpGlucoseColors.IN_RANGE
            GlucoseRange.HIGH, GlucoseRange.VERY_HIGH -> ALERT_ORANGE
            GlucoseRange.LOW, GlucoseRange.VERY_LOW -> AgpGlucoseColors.LOW
            GlucoseRange.UNKNOWN -> AgpGlucoseColors.UNKNOWN
        }

    /** Apply alpha while keeping RGB (stale fade). */
    fun withOpacity(argb: Int, opacity: Float): Int {
        val alpha = (opacity.coerceIn(0f, 1f) * 255f).roundToInt().coerceIn(0, 255)
        return (alpha shl 24) or (argb and 0x00FFFFFF)
    }

    /** Force opaque 0xAARRGGBB when alpha channel is missing (0). */
    fun opaqueArgb(argb: Int): Int =
        if ((argb ushr 24) == 0) argb or 0xFF000000.toInt() else argb
}
