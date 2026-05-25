package com.widgetg7.core.model

/** AGP / Time-in-Range glucose band for medical UI coloring. */
enum class GlucoseRange {
    VERY_LOW,
    LOW,
    IN_RANGE,
    HIGH,
    VERY_HIGH,
    UNKNOWN,
}

object AgpGlucoseColors {
    const val VERY_LOW = 0xFFC54B47.toInt()
    const val LOW = 0xFFD46363.toInt()
    const val IN_RANGE = 0xFF3A9B6E.toInt()
    const val HIGH = 0xFFA8742F.toInt()
    const val VERY_HIGH = 0xFFC9743F.toInt()
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

    fun resolveColorForReading(valueMgDl: Int?, stale: Boolean): Int {
        if (valueMgDl == null || stale) return AgpGlucoseColors.UNKNOWN
        return resolveColor(valueMgDl)
    }
}
