package com.glucoseforwatch.core.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GlucoseFreshnessTest {
    private val now = 1_700_000_000_000L

    @Test
    fun readingWithinThreshold_isFresh() {
        assertFalse(GlucoseFreshness.isStale(now - 6 * 60_000L, now))
        assertFalse(GlucoseFreshness.isStale(now - GlucoseFreshness.STALE_AFTER_MS, now))
    }

    @Test
    fun readingPastThreshold_isStale() {
        assertTrue(GlucoseFreshness.isStale(now - GlucoseFreshness.STALE_AFTER_MS - 1, now))
    }

    @Test
    fun missingTimestamp_isStale() {
        assertTrue(GlucoseFreshness.isStale(0L, now))
    }
}
