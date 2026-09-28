package com.glucoseforwatch.core.model

import androidx.annotation.Keep

/** AGP / Time-in-Range glucose band for medical UI coloring. */
@Keep
enum class GlucoseRange {
    VERY_LOW,
    LOW,
    IN_RANGE,
    HIGH,
    VERY_HIGH,
    UNKNOWN,
}

/**
 * ARGB medical palette for glucose values.
 *
 * Blue is banned for glycemic display. In-range must read as frank green;
 * hypo as vivid red; hyper as yellow then orange.
 */
@Keep
object AgpGlucoseColors {
    /** Vivid alert red — VERY_LOW and LOW. */
    const val VERY_LOW = 0xFFD32F2F.toInt()
    const val LOW = 0xFFD32F2F.toInt()
    /** Frank green — in target / IN_RANGE (never Material primary blue). */
    const val IN_RANGE = 0xFF008000.toInt()
    /** Alert yellow — HIGH. */
    const val HIGH = 0xFFFFCC00.toInt()
    /** Alert orange — VERY_HIGH. */
    const val VERY_HIGH = 0xFFFF9900.toInt()
    /** Neutral gray — stale / missing (never blue). */
    const val UNKNOWN = 0xFF9A948D.toInt()

    fun colorFor(range: GlucoseRange): Int =
        when (range) {
            GlucoseRange.VERY_LOW -> VERY_LOW
            GlucoseRange.LOW -> LOW
            GlucoseRange.IN_RANGE -> IN_RANGE
            GlucoseRange.HIGH -> HIGH
            GlucoseRange.VERY_HIGH -> VERY_HIGH
            GlucoseRange.UNKNOWN -> UNKNOWN
        }
}

/** Maps mg/dL values to [GlucoseRange] bands and AGP display colors. */
@Keep
object GlucoseRangeResolver {
    fun resolveRange(mgDl: Int): GlucoseRange =
        when {
            mgDl < 54 -> GlucoseRange.VERY_LOW
            mgDl < 70 -> GlucoseRange.LOW
            mgDl <= 180 -> GlucoseRange.IN_RANGE
            mgDl <= 250 -> GlucoseRange.HIGH
            else -> GlucoseRange.VERY_HIGH
        }

    fun resolveColor(mgDl: Int): Int = AgpGlucoseColors.colorFor(resolveRange(mgDl))

    /** Returns [AgpGlucoseColors.UNKNOWN] when value is missing or the reading is stale. */
    fun resolveColorForReading(valueMgDl: Int?, stale: Boolean): Int {
        if (valueMgDl == null || stale) return AgpGlucoseColors.UNKNOWN
        return resolveColor(valueMgDl)
    }
}
