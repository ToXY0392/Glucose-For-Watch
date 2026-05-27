package com.glucoseforwatch.feature.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncReadingTextFormatterTest {
    @Test
    fun returns_empty_label_for_invalid_epoch() {
        assertEquals("", SyncReadingTextFormatter.readingAgeLabel(readingEpochMs = 0L, nowEpochMs = 1_000L))
    }

    @Test
    fun returns_instant_label_for_recent_reading() {
        assertEquals(
            "à l'instant",
            SyncReadingTextFormatter.readingAgeLabel(readingEpochMs = 100_000L, nowEpochMs = 100_500L),
        )
    }

    @Test
    fun returns_minute_label_for_one_minute_old_reading() {
        assertEquals(
            "il y a 1 min",
            SyncReadingTextFormatter.readingAgeLabel(readingEpochMs = 100_000L, nowEpochMs = 160_000L),
        )
    }

    @Test
    fun returns_plural_minutes_for_older_reading() {
        assertEquals(
            "il y a 5 min",
            SyncReadingTextFormatter.readingAgeLabel(readingEpochMs = 100_000L, nowEpochMs = 400_000L),
        )
    }
}
