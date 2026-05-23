package com.widgetg7.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class GlucoseRangeResolverTest {
    @Test
    fun resolveRange_matchesAgpThresholds() {
        assertEquals(GlucoseRange.VERY_LOW, GlucoseRangeResolver.resolveRange(53))
        assertEquals(GlucoseRange.LOW, GlucoseRangeResolver.resolveRange(69))
        assertEquals(GlucoseRange.IN_RANGE, GlucoseRangeResolver.resolveRange(120))
        assertEquals(GlucoseRange.HIGH, GlucoseRangeResolver.resolveRange(200))
        assertEquals(GlucoseRange.VERY_HIGH, GlucoseRangeResolver.resolveRange(300))
    }

    @Test
    fun resolveColor_returnsAgpPalette() {
        assertEquals(AgpGlucoseColors.IN_RANGE, GlucoseRangeResolver.resolveColor(120))
        assertEquals(AgpGlucoseColors.HIGH, GlucoseRangeResolver.resolveColor(200))
        assertEquals(AgpGlucoseColors.LOW, GlucoseRangeResolver.resolveColor(60))
    }

    @Test
    fun resolveColorForReading_staleUsesUnknown() {
        assertEquals(AgpGlucoseColors.UNKNOWN, GlucoseRangeResolver.resolveColorForReading(120, stale = true))
    }
}
