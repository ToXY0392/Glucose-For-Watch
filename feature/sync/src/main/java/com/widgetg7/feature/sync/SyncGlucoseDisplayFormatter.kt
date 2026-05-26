package com.widgetg7.feature.sync

import com.widgetg7.core.model.SyncStatusSnapshot

/** Formats glucose values and home-screen summaries (LOW/HI clamping, trend, age). */
object SyncGlucoseDisplayFormatter {
    private const val DISPLAY_LOW_MAX = 40
    private const val DISPLAY_HIGH_MIN = 400

    fun formatValueMgDl(value: Int): String =
        when {
            value <= DISPLAY_LOW_MAX -> "LOW"
            value >= DISPLAY_HIGH_MIN -> "HI"
            else -> value.toString()
        }

    fun homeValuePrimary(snapshot: SyncStatusSnapshot): String? {
        val v = snapshot.lastValueMgDl ?: return null
        return "${formatValueMgDl(v)} mg/dL"
    }

    fun homeReadingSummary(snapshot: SyncStatusSnapshot): String? {
        val v = snapshot.lastValueMgDl ?: return null
        val valueText = formatValueMgDl(v)
        val trend = snapshot.lastTrend
        val trendLabel = SyncTrendTextFormatter.displayTrend(trend).trim()
        val age = SyncReadingTextFormatter.readingAgeLabel(snapshot.lastReadingTimestampEpochMs)
        return buildString {
            append(valueText).append(" mg/dL")
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
