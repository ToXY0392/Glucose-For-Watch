package com.glucoseforwatch.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** Every AGP threshold maps to its range and to the standard AGP color. */
class GlucoseAgpThresholdsTest {
    @Test
    fun everyThreshold_mapsToItsRangeAndStandardColor() {
        val cases = mapOf(
            53 to (GlucoseRange.VERY_LOW to AgpGlucoseColors.VERY_LOW),
            54 to (GlucoseRange.LOW to AgpGlucoseColors.LOW),
            69 to (GlucoseRange.LOW to AgpGlucoseColors.LOW),
            70 to (GlucoseRange.IN_RANGE to AgpGlucoseColors.IN_RANGE),
            180 to (GlucoseRange.IN_RANGE to AgpGlucoseColors.IN_RANGE),
            181 to (GlucoseRange.HIGH to AgpGlucoseColors.HIGH),
            250 to (GlucoseRange.HIGH to AgpGlucoseColors.HIGH),
            251 to (GlucoseRange.VERY_HIGH to AgpGlucoseColors.VERY_HIGH),
        )
        cases.forEach { (mgDl, expected) ->
            assertEquals("range for $mgDl", expected.first, GlucoseRangeResolver.resolveRange(mgDl))
            assertEquals("color for $mgDl", expected.second, GlucoseRangeResolver.resolveColor(mgDl))
        }
    }

    @Test
    fun standardPalette_isTheDiabetesColorCode() {
        assertEquals(0xFFD32F2F.toInt(), AgpGlucoseColors.LOW)
        assertEquals(0xFF008000.toInt(), AgpGlucoseColors.IN_RANGE)
        assertEquals(0xFFFFCC00.toInt(), AgpGlucoseColors.HIGH)
        assertEquals(0xFFFF9900.toInt(), AgpGlucoseColors.VERY_HIGH)
    }

    @Test
    fun staleOrMissingReading_usesUnknown() {
        assertEquals(AgpGlucoseColors.UNKNOWN, GlucoseRangeResolver.resolveColorForReading(120, stale = true))
        assertEquals(AgpGlucoseColors.UNKNOWN, GlucoseRangeResolver.resolveColorForReading(null, stale = false))
    }
}
