package com.glucoseforwatch.feature.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class GlucosePollSchedulerTest {
    private val readingAt = 1_700_000_000_000L
    private val overdue = BatteryDegradedPolicy.POLL_INTERVAL_NORMAL_MS

    @Test
    fun noReadingYet_pollsAtOverdueInterval() {
        assertEquals(overdue, GlucosePollScheduler.nextDelayMs(readingAt, 0L, overdue))
    }

    @Test
    fun freshReading_waitsUntilNextExpectedReading() {
        val now = readingAt + 60_000L
        assertEquals(
            GlucosePollScheduler.READING_PERIOD_MS + GlucosePollScheduler.UPLOAD_MARGIN_MS - 60_000L,
            GlucosePollScheduler.nextDelayMs(now, readingAt, overdue),
        )
    }

    @Test
    fun nextReadingDueShortly_waitsOnlyTheRemainder() {
        val now = readingAt + GlucosePollScheduler.READING_PERIOD_MS
        assertEquals(
            GlucosePollScheduler.UPLOAD_MARGIN_MS,
            GlucosePollScheduler.nextDelayMs(now, readingAt, overdue),
        )
    }

    @Test
    fun overdueReading_pollsAtOverdueInterval() {
        val now = readingAt + 6 * 60_000L
        assertEquals(overdue, GlucosePollScheduler.nextDelayMs(now, readingAt, overdue))
        assertEquals(
            BatteryDegradedPolicy.POLL_INTERVAL_DEGRADED_MS,
            GlucosePollScheduler.nextDelayMs(now, readingAt, BatteryDegradedPolicy.POLL_INTERVAL_DEGRADED_MS),
        )
    }

    @Test
    fun readingTimestampInTheFuture_isClampedToOnePeriod() {
        val now = readingAt - 10 * 60_000L
        assertEquals(
            GlucosePollScheduler.READING_PERIOD_MS + GlucosePollScheduler.UPLOAD_MARGIN_MS,
            GlucosePollScheduler.nextDelayMs(now, readingAt, overdue),
        )
    }
}
