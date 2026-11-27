package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.ui.graphics.Color
import com.glucoseforwatch.core.model.AgpGlucoseColors
import com.glucoseforwatch.core.model.GlucoseRange
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeGlucoseColorTest {
    @Test
    fun freshReading_usesTheStandardAgpColorOfItsRange() {
        assertEquals(Color(AgpGlucoseColors.VERY_LOW), HomeGlucoseColor.color(50, stale = false))
        assertEquals(Color(AgpGlucoseColors.LOW), HomeGlucoseColor.color(60, stale = false))
        assertEquals(Color(AgpGlucoseColors.IN_RANGE), HomeGlucoseColor.color(124, stale = false))
        assertEquals(Color(AgpGlucoseColors.HIGH), HomeGlucoseColor.color(212, stale = false))
        assertEquals(Color(AgpGlucoseColors.VERY_HIGH), HomeGlucoseColor.color(278, stale = false))
    }

    @Test
    fun staleOrMissingReading_usesUnknown() {
        assertEquals(Color(AgpGlucoseColors.UNKNOWN), HomeGlucoseColor.color(124, stale = true))
        assertEquals(Color(AgpGlucoseColors.UNKNOWN), HomeGlucoseColor.color(null, stale = false))
        assertEquals(GlucoseRange.UNKNOWN, HomeGlucoseColor.range(124, stale = true))
    }

    @Test
    fun scaleSegments_followTheAgpRangesInOrder() {
        val colors = HomeGlucoseColor.scaleSegments.map { it.color }
        assertEquals(
            listOf(
                Color(AgpGlucoseColors.VERY_LOW),
                Color(AgpGlucoseColors.LOW),
                Color(AgpGlucoseColors.IN_RANGE),
                Color(AgpGlucoseColors.HIGH),
                Color(AgpGlucoseColors.VERY_HIGH),
            ),
            colors,
        )
        assertEquals(360f, HomeGlucoseColor.scaleSegments.sumOf { it.spanMgDl.toDouble() }.toFloat())
        assertEquals(0f, HomeGlucoseColor.scaleFraction(30))
        assertEquals(1f, HomeGlucoseColor.scaleFraction(500))
    }
}
