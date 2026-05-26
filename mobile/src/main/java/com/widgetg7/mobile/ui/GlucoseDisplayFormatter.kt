package com.widgetg7.mobile.ui

import com.widgetg7.core.model.GlucoseDisplayUnit
import com.widgetg7.core.model.GlucoseUnitFormatter
import com.widgetg7.core.model.SyncStatusSnapshot
import com.widgetg7.feature.sync.SyncGlucoseDisplayFormatter
import com.widgetg7.feature.sync.SyncReadingTextFormatter

/** Phone UI delegates for glucose value and home hero text formatting. */
object GlucoseDisplayFormatter {
    fun formatValue(valueMgDl: Int, unit: GlucoseDisplayUnit): String =
        SyncGlucoseDisplayFormatter.formatValue(valueMgDl, unit)

    fun formatValueMgDl(value: Int): String = SyncGlucoseDisplayFormatter.formatValueMgDl(value)

    fun homeValuePrimary(snapshot: SyncStatusSnapshot, unit: GlucoseDisplayUnit): String? =
        SyncGlucoseDisplayFormatter.homeValuePrimary(snapshot, unit)

    fun homeReadingSummary(snapshot: SyncStatusSnapshot, unit: GlucoseDisplayUnit): String? =
        SyncGlucoseDisplayFormatter.homeReadingSummary(snapshot, unit)

    fun homeValueSubtitle(snapshot: SyncStatusSnapshot): String? =
        SyncGlucoseDisplayFormatter.homeValueSubtitle(snapshot)

    fun unitLabel(unit: GlucoseDisplayUnit): String = GlucoseUnitFormatter.unitLabel(unit)

    fun readingAgeLabel(readingEpochMs: Long, nowEpochMs: Long = System.currentTimeMillis()): String {
        return SyncReadingTextFormatter.readingAgeLabel(
            readingEpochMs = readingEpochMs,
            nowEpochMs = nowEpochMs,
        )
    }
}
