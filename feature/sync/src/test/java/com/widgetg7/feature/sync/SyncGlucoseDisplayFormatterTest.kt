package com.widgetg7.feature.sync

import com.widgetg7.core.model.GlucoseDisplayUnit
import com.widgetg7.core.model.SyncErrorCategory
import com.widgetg7.core.model.SyncStatusSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncGlucoseDisplayFormatterTest {
    @Test
    fun format_value_handles_low_hi_and_normal() {
        assertEquals("LOW", SyncGlucoseDisplayFormatter.formatValueMgDl(40))
        assertEquals("HI", SyncGlucoseDisplayFormatter.formatValueMgDl(400))
        assertEquals("123", SyncGlucoseDisplayFormatter.formatValueMgDl(123))
    }

    @Test
    fun home_fields_return_null_when_no_value() {
        val snapshot = snapshot(lastValueMgDl = null)
        assertNull(SyncGlucoseDisplayFormatter.homeValuePrimary(snapshot, GlucoseDisplayUnit.MG_DL))
        assertNull(SyncGlucoseDisplayFormatter.homeReadingSummary(snapshot, GlucoseDisplayUnit.MG_DL))
        assertNull(SyncGlucoseDisplayFormatter.homeValueSubtitle(snapshot))
    }

    @Test
    fun home_primary_formats_with_unit() {
        val snapshot = snapshot(lastValueMgDl = 100)
        assertEquals("100 mg/dL", SyncGlucoseDisplayFormatter.homeValuePrimary(snapshot, GlucoseDisplayUnit.MG_DL))
        assertEquals("5.5 mmol/L", SyncGlucoseDisplayFormatter.homeValuePrimary(snapshot, GlucoseDisplayUnit.MMOL_L))
    }

    @Test
    fun home_summary_includes_translated_trend_and_age() {
        val snapshot = snapshot(lastValueMgDl = 120, lastTrend = "UP", lastReadingTimestampEpochMs = 200_000L)
        val computed = SyncGlucoseDisplayFormatter.homeReadingSummary(snapshot, GlucoseDisplayUnit.MG_DL)
        // We only assert stable parts because formatter calls current time internally.
        requireNotNull(computed)
        assertTrue(computed.startsWith("120 mg/dL · en hausse"))
    }

    private fun snapshot(
        lastValueMgDl: Int?,
        lastTrend: String = "FLAT",
        lastReadingTimestampEpochMs: Long = 0L,
    ) = SyncStatusSnapshot(
        lastValueMgDl = lastValueMgDl,
        lastTrend = lastTrend,
        lastSourceName = "",
        lastSyncEpochMs = 0L,
        lastReadingTimestampEpochMs = lastReadingTimestampEpochMs,
        lastError = "",
        lastErrorCategory = SyncErrorCategory.NONE,
        authFailureCount = 0,
        consecutiveFailureCount = 0,
    )
}
