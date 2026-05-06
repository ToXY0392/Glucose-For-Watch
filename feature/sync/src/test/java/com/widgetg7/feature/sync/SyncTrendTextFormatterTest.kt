package com.widgetg7.feature.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncTrendTextFormatterTest {
    @Test
    fun known_trends_are_translated() {
        assertEquals("en hausse", SyncTrendTextFormatter.displayTrend("UP"))
        assertEquals("en hausse légère", SyncTrendTextFormatter.displayTrend("UP_RIGHT"))
        assertEquals("stable", SyncTrendTextFormatter.displayTrend("FLAT"))
        assertEquals("en baisse légère", SyncTrendTextFormatter.displayTrend("DOWN_RIGHT"))
        assertEquals("en baisse", SyncTrendTextFormatter.displayTrend("DOWN"))
    }

    @Test
    fun unknown_trend_is_preserved() {
        assertEquals("CUSTOM", SyncTrendTextFormatter.displayTrend("CUSTOM"))
    }
}
