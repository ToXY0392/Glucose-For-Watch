package com.widgetg7.mobile.ui

import com.widgetg7.core.model.SyncStatusSnapshot
import com.widgetg7.feature.sync.SyncGlucoseDisplayFormatter
import com.widgetg7.feature.sync.SyncReadingTextFormatter

object GlucoseDisplayFormatter {
    fun formatValueMgDl(value: Int): String = SyncGlucoseDisplayFormatter.formatValueMgDl(value)

    /** Grande ligne d’accueil : « 120 mg/dL » ou LOW / HI. */
    fun homeValuePrimary(snapshot: SyncStatusSnapshot): String? = SyncGlucoseDisplayFormatter.homeValuePrimary(snapshot)

    /** Sous-ligne tendance · âge. */
    fun homeReadingSummary(snapshot: SyncStatusSnapshot): String? = SyncGlucoseDisplayFormatter.homeReadingSummary(snapshot)

    fun homeValueSubtitle(snapshot: SyncStatusSnapshot): String? = SyncGlucoseDisplayFormatter.homeValueSubtitle(snapshot)

    fun readingAgeLabel(readingEpochMs: Long, nowEpochMs: Long = System.currentTimeMillis()): String {
        return SyncReadingTextFormatter.readingAgeLabel(
            readingEpochMs = readingEpochMs,
            nowEpochMs = nowEpochMs,
        )
    }
}
