package com.glucoseforwatch.feature.dexcomshare

import org.junit.Assert.assertEquals
import org.junit.Test

class DexcomTrendMappingTest {
    @Test
    fun numeric_codes_map_to_tokens() {
        assertEquals("UP", mapDexcomTrend(1))
        assertEquals("UP", mapDexcomTrend(2))
        assertEquals("UP_RIGHT", mapDexcomTrend(3))
        assertEquals("FLAT", mapDexcomTrend(4))
        assertEquals("DOWN_RIGHT", mapDexcomTrend(5))
        assertEquals("DOWN", mapDexcomTrend(6))
        assertEquals("DOWN", mapDexcomTrend(7))
    }

    @Test
    fun names_map_to_tokens() {
        assertEquals("UP", mapDexcomTrend("DoubleUp"))
        assertEquals("UP_RIGHT", mapDexcomTrend("FortyFiveUp"))
        assertEquals("FLAT", mapDexcomTrend("Flat"))
        assertEquals("DOWN_RIGHT", mapDexcomTrend("FortyFiveDown"))
        assertEquals("DOWN", mapDexcomTrend("SINGLE_DOWN"))
    }

    @Test
    fun missing_or_unusable_trend_is_none_not_flat() {
        assertEquals("NONE", mapDexcomTrend(null))
        assertEquals("NONE", mapDexcomTrend(0))
        assertEquals("NONE", mapDexcomTrend(8))
        assertEquals("NONE", mapDexcomTrend(9))
        assertEquals("NONE", mapDexcomTrend("None"))
        assertEquals("NONE", mapDexcomTrend("NotComputable"))
        assertEquals("NONE", mapDexcomTrend("RateOutOfRange"))
        assertEquals("NONE", mapDexcomTrend("garbage"))
    }
}
