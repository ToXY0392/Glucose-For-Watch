package com.widgetg7.feature.sync

import com.widgetg7.core.model.GlucoseDisplayUnit
import com.widgetg7.core.model.GlucoseUnitFormatter
import com.widgetg7.core.model.SyncStatusSnapshot

/** Formats glucose values and home-screen summaries (LOW/HI clamping, trend, age). */
object SyncGlucoseDisplayFormatter {
    fun formatValue(valueMgDl: Int, unit: GlucoseDisplayUnit = GlucoseDisplayUnit.MG_DL): String =
        GlucoseUnitFormatter.formatValue(valueMgDl, unit)

    fun formatValueMgDl(value: Int): String = formatValue(value, GlucoseDisplayUnit.MG_DL)

    fun homeValuePrimary(snapshot: SyncStatusSnapshot, unit: GlucoseDisplayUnit): String? {
        val v = snapshot.lastValueMgDl ?: return null
        return GlucoseUnitFormatter.formatWithUnit(v, unit)
    }

    fun homeReadingSummary(snapshot: SyncStatusSnapshot, unit: GlucoseDisplayUnit): String? {
        val v = snapshot.lastValueMgDl ?: return null
        val valueText = GlucoseUnitFormatter.formatWithUnit(v, unit)
        val trend = snapshot.lastTrend
        val trendLabel = SyncTrendTextFormatter.displayTrend(trend).trim()
        val age = SyncReadingTextFormatter.readingAgeLabel(snapshot.lastReadingTimestampEpochMs)
        return buildString {
            append(valueText)
            if (trendLabel.isNotBlank() && trendLabel != trend) {
                append(" · ").append(trendLabel)
            }
            if (age.isNotBlank()) {
                append(" · ").append(age)
            }
        }
    }

    fun homeValueSubtitle(snapshot: SyncStatusSnapshot): String? {
        snapshot.lastValueMgDl ?: return null
        val trendLabel = SyncTrendTextFormatter.displayTrend(snapshot.lastTrend).trim()
        val age = SyncReadingTextFormatter.readingAgeLabel(snapshot.lastReadingTimestampEpochMs)
        val parts = mutableListOf<String>()
        if (trendLabel.isNotBlank()) parts += trendLabel
        if (age.isNotBlank()) parts += age
        if (parts.isEmpty()) return null
        return parts.joinToString(" · ")
    }
}
