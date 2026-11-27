package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.ui.graphics.Color
import com.glucoseforwatch.core.model.AgpGlucoseColors
import com.glucoseforwatch.core.model.GlucoseRange
import com.glucoseforwatch.core.model.GlucoseRangeResolver

/**
 * Glucose colors for the home glass cards: AGP light-surface shades only, through
 * [GlucoseRangeResolver]. A stale or missing reading is drawn in the AGP unknown color.
 */
internal object HomeGlucoseColor {
    const val SCALE_MIN_MG_DL = 40
    const val SCALE_MAX_MG_DL = 400

    data class ScaleSegment(val spanMgDl: Float, val color: Color, val alpha: Float = 1f)

    fun color(valueMgDl: Int?, stale: Boolean): Color =
        Color(GlucoseRangeResolver.resolveColorForReadingOnLight(valueMgDl, stale))

    fun range(valueMgDl: Int?, stale: Boolean): GlucoseRange =
        if (valueMgDl == null || stale) GlucoseRange.UNKNOWN else GlucoseRangeResolver.resolveRange(valueMgDl)

    /** 40–54, 54–70, 70–180, 180–250, 250–400 mg/dL, in AGP light-surface shades. */
    val scaleSegments: List<ScaleSegment> =
        listOf(
            ScaleSegment(14f, Color(AgpGlucoseColors.colorOnLightFor(GlucoseRange.VERY_LOW))),
            ScaleSegment(16f, Color(AgpGlucoseColors.colorOnLightFor(GlucoseRange.LOW)), alpha = 0.7f),
            ScaleSegment(110f, Color(AgpGlucoseColors.colorOnLightFor(GlucoseRange.IN_RANGE))),
            ScaleSegment(70f, Color(AgpGlucoseColors.colorOnLightFor(GlucoseRange.HIGH))),
            ScaleSegment(150f, Color(AgpGlucoseColors.colorOnLightFor(GlucoseRange.VERY_HIGH))),
        )

    /** Clinical thresholds labelled under the bar (the 40 and 400 ends stay unlabelled). */
    val scaleTicksMgDl = listOf(70, 180, 250)

    fun scaleFraction(valueMgDl: Int): Float =
        ((valueMgDl - SCALE_MIN_MG_DL).toFloat() / (SCALE_MAX_MG_DL - SCALE_MIN_MG_DL)).coerceIn(0f, 1f)
}
