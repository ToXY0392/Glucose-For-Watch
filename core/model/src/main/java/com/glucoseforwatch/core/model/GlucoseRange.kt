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

    /*
     * Light-surface shades (phone glass cards on a pale background). Same hues as above;
     * yellow, orange and grey are darkened because the base shades fall under 3:1 contrast
     * on a light surface. Red and green already pass and stay identical.
     */
    /** Dark amber — HIGH on a light surface. */
    const val HIGH_ON_LIGHT = 0xFFA87C00.toInt()
    /** Burnt orange — VERY_HIGH on a light surface. */
    const val VERY_HIGH_ON_LIGHT = 0xFFB85C00.toInt()
    /** Darker neutral gray — stale / missing on a light surface. */
    const val UNKNOWN_ON_LIGHT = 0xFF77716A.toInt()

    fun colorFor(range: GlucoseRange): Int =
        when (range) {
            GlucoseRange.VERY_LOW -> VERY_LOW
            GlucoseRange.LOW -> LOW
            GlucoseRange.IN_RANGE -> IN_RANGE
            GlucoseRange.HIGH -> HIGH
            GlucoseRange.VERY_HIGH -> VERY_HIGH
            GlucoseRange.UNKNOWN -> UNKNOWN
        }

    /** Same mapping as [colorFor], for a light surface. */
    fun colorOnLightFor(range: GlucoseRange): Int =
        when (range) {
            GlucoseRange.HIGH -> HIGH_ON_LIGHT
            GlucoseRange.VERY_HIGH -> VERY_HIGH_ON_LIGHT
            GlucoseRange.UNKNOWN -> UNKNOWN_ON_LIGHT
            else -> colorFor(range)
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

    /** AGP color for a value drawn on a light surface. */
    fun resolveColorOnLight(mgDl: Int): Int = AgpGlucoseColors.colorOnLightFor(resolveRange(mgDl))

    /** Light-surface variant of [resolveColorForReading]. */
    fun resolveColorForReadingOnLight(valueMgDl: Int?, stale: Boolean): Int {
        if (valueMgDl == null || stale) return AgpGlucoseColors.UNKNOWN_ON_LIGHT
        return resolveColorOnLight(valueMgDl)
    }
}
