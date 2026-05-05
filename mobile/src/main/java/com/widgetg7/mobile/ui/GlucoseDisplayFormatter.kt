package com.widgetg7.mobile.ui

import com.widgetg7.mobile.status.SyncStatusSnapshot
import com.widgetg7.mobile.sync.SyncText
import kotlin.math.max

object GlucoseDisplayFormatter {
    private const val DISPLAY_LOW_MAX = 40
    private const val DISPLAY_HIGH_MIN = 400

    fun formatValueMgDl(value: Int): String =
        when {
            value <= DISPLAY_LOW_MAX -> "LOW"
            value >= DISPLAY_HIGH_MIN -> "HI"
            else -> value.toString()
        }

    /** Grande ligne d’accueil : « 120 mg/dL » ou LOW / HI. */
    fun homeValuePrimary(snapshot: SyncStatusSnapshot): String? {
        val v = snapshot.lastValueMgDl ?: return null
        return "${formatValueMgDl(v)} mg/dL"
    }

    /** Sous-ligne tendance · âge. */
    fun homeReadingSummary(snapshot: SyncStatusSnapshot): String? {
        val v = snapshot.lastValueMgDl ?: return null
        val valueText = formatValueMgDl(v)
        val trend = snapshot.lastTrend
        val trendLabel = SyncText.displayTrend(trend).trim()
        val age = readingAgeLabel(snapshot.lastReadingTimestampEpochMs)
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
        val v = snapshot.lastValueMgDl ?: return null
        val trendLabel = SyncText.displayTrend(snapshot.lastTrend).trim()
        val age = readingAgeLabel(snapshot.lastReadingTimestampEpochMs)
        val parts = mutableListOf<String>()
        if (trendLabel.isNotBlank()) parts += trendLabel
        if (age.isNotBlank()) parts += age
        if (parts.isEmpty()) return null
        return parts.joinToString(" · ")
    }

    fun readingAgeLabel(readingEpochMs: Long, nowEpochMs: Long = System.currentTimeMillis()): String {
        if (readingEpochMs <= 0L) return ""
        val ageMinutes = max(0L, (nowEpochMs - readingEpochMs) / 60_000L)
        return when (ageMinutes) {
            0L -> "à l'instant"
            1L -> "il y a 1 min"
            else -> "il y a $ageMinutes min"
        }
    }
}
