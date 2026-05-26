package com.widgetg7.wear.complication

import com.widgetg7.core.model.AgpGlucoseColors
import com.widgetg7.core.model.GlucoseRangeResolver
import org.junit.Assert.assertEquals
import org.junit.Test

class AgpComplicationColorRampTest {

    @Test
    fun freshReading_usesAgpColorsAtRangeStops() {
        val ramp = AgpComplicationColorRamp.forGlucoseRange(40f, 400f, stale = false)

        assertEquals(AgpGlucoseColors.VERY_LOW, ramp.colors.first())
        assertEquals(
            GlucoseRangeResolver.resolveColor(200),
            ramp.colors[ramp.colors.lastIndex / 2],
        )
    }

    @Test
    fun staleReading_usesUnknownGray() {
        val ramp = AgpComplicationColorRamp.forGlucoseRange(40f, 400f, stale = true)

        assertEquals(AgpGlucoseColors.UNKNOWN, ramp.colors.first())
        assertEquals(AgpGlucoseColors.UNKNOWN, ramp.colors.last())
    }
}
