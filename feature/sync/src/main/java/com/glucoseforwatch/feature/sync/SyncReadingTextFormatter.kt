package com.glucoseforwatch.feature.sync

import kotlin.math.max

/** Formats reading age as a relative French label (e.g. "il y a 5 min"). */
object SyncReadingTextFormatter {
    fun readingAgeLabel(
        readingEpochMs: Long,
        nowEpochMs: Long = System.currentTimeMillis(),
    ): String {
        if (readingEpochMs <= 0L) return ""
        val ageMinutes = max(0L, (nowEpochMs - readingEpochMs) / 60_000L)
        return when (ageMinutes) {
            0L -> "à l'instant"
            1L -> "il y a 1 min"
            else -> "il y a $ageMinutes min"
        }
    }
}
