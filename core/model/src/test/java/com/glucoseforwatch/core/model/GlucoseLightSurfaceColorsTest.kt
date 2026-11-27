package com.glucoseforwatch.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** Every AGP threshold, on the base palette and on the light-surface palette. */
class GlucoseLightSurfaceColorsTest {
    @Test
    fun everyThreshold_mapsToTheSameRangeOnBothPalettes() {
        val cases = mapOf(
            53 to GlucoseRange.VERY_LOW,
            54 to GlucoseRange.LOW,
            69 to GlucoseRange.LOW,
            70 to GlucoseRange.IN_RANGE,
            180 to GlucoseRange.IN_RANGE,
            181 to GlucoseRange.HIGH,
            250 to GlucoseRange.HIGH,
            251 to GlucoseRange.VERY_HIGH,
        )
        cases.forEach { (mgDl, range) ->
            assertEquals("range for $mgDl", range, GlucoseRangeResolver.resolveRange(mgDl))
            assertEquals("base color for $mgDl", AgpGlucoseColors.colorFor(range), GlucoseRangeResolver.resolveColor(mgDl))
            assertEquals("light color for $mgDl", AgpGlucoseColors.colorOnLightFor(range), GlucoseRangeResolver.resolveColorOnLight(mgDl))
        }
    }

    @Test
    fun lightSurface_keepsRedAndGreen_darkensYellowOrangeAndGrey() {
        assertEquals(AgpGlucoseColors.VERY_LOW, AgpGlucoseColors.colorOnLightFor(GlucoseRange.VERY_LOW))
        assertEquals(AgpGlucoseColors.LOW, AgpGlucoseColors.colorOnLightFor(GlucoseRange.LOW))
        assertEquals(AgpGlucoseColors.IN_RANGE, AgpGlucoseColors.colorOnLightFor(GlucoseRange.IN_RANGE))
        assertEquals(AgpGlucoseColors.HIGH_ON_LIGHT, AgpGlucoseColors.colorOnLightFor(GlucoseRange.HIGH))
        assertEquals(AgpGlucoseColors.VERY_HIGH_ON_LIGHT, AgpGlucoseColors.colorOnLightFor(GlucoseRange.VERY_HIGH))
        assertEquals(AgpGlucoseColors.UNKNOWN_ON_LIGHT, AgpGlucoseColors.colorOnLightFor(GlucoseRange.UNKNOWN))
    }

    @Test
    fun lightSurface_staleOrMissingReading_usesUnknown() {
        assertEquals(AgpGlucoseColors.UNKNOWN_ON_LIGHT, GlucoseRangeResolver.resolveColorForReadingOnLight(120, stale = true))
        assertEquals(AgpGlucoseColors.UNKNOWN_ON_LIGHT, GlucoseRangeResolver.resolveColorForReadingOnLight(null, stale = false))
        assertEquals(AgpGlucoseColors.IN_RANGE, GlucoseRangeResolver.resolveColorForReadingOnLight(120, stale = false))
    }
}
