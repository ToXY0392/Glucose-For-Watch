package com.glucoseforwatch.wear.complication

import androidx.wear.watchface.complications.data.ColorRamp
import com.glucoseforwatch.core.model.AgpGlucoseColors
import com.glucoseforwatch.core.model.GlucoseRangeResolver
import kotlin.math.roundToInt

/** AGP medical color ramp for [androidx.wear.watchface.complications.data.RangedValueComplicationData]. */
internal object AgpComplicationColorRamp {
    private const val STOP_COUNT = 13

    fun forGlucoseRange(
        minMgDl: Float,
        maxMgDl: Float,
        stale: Boolean,
    ): ColorRamp {
        if (stale) {
            return ColorRamp(IntArray(STOP_COUNT) { AgpGlucoseColors.UNKNOWN }, false)
        }
        val colors =
            IntArray(STOP_COUNT) { index ->
                val mgDl =
                    (minMgDl + (maxMgDl - minMgDl) * index / (STOP_COUNT - 1))
                        .roundToInt()
                        .coerceIn(minMgDl.roundToInt(), maxMgDl.roundToInt())
                GlucoseRangeResolver.resolveColor(mgDl)
            }
        return ColorRamp(colors, false)
    }
}
