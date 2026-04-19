package com.widgetg7.wear.data

import android.content.Context

enum class GlucoseSemanticLevel {
    NORMAL,
    ATTENTION,
    ALERT,
    STALE,
}

data class GlucoseSnapshot(
    val valueMgDl: Int,
    val trend: String,
    val deltaMgDl: Int,
    val timestampEpochMs: Long,
    val stale: Boolean,
) {
    fun trendArrow(): String = when (trend) {
        "UP" -> "↑"
        "UP_RIGHT" -> "↗"
        "FLAT" -> "→"
        "DOWN_RIGHT" -> "↘"
        "DOWN" -> "↓"
        else -> "?"
    }

    fun signedDelta(): String = if (deltaMgDl >= 0) "+$deltaMgDl" else deltaMgDl.toString()

    fun semanticLevel(): GlucoseSemanticLevel {
        if (stale) return GlucoseSemanticLevel.STALE
        return when {
            valueMgDl < 70 || valueMgDl > 250 -> GlucoseSemanticLevel.ALERT
            valueMgDl in 70..79 || valueMgDl in 181..250 -> GlucoseSemanticLevel.ATTENTION
            else -> GlucoseSemanticLevel.NORMAL
        }
    }

    fun semanticColorArgb(): Int = when (semanticLevel()) {
        GlucoseSemanticLevel.NORMAL -> 0xFFF5F7FA.toInt()
        GlucoseSemanticLevel.ATTENTION -> 0xFFF4B942.toInt()
        GlucoseSemanticLevel.ALERT -> 0xFFFF5A5F.toInt()
        GlucoseSemanticLevel.STALE -> 0xFFA7B0BA.toInt()
    }

    fun metadataColorArgb(): Int = when (semanticLevel()) {
        GlucoseSemanticLevel.NORMAL -> 0xFF7FDBB6.toInt()
        GlucoseSemanticLevel.ATTENTION -> 0xFFF4B942.toInt()
        GlucoseSemanticLevel.ALERT -> 0xFFFF8A8F.toInt()
        GlucoseSemanticLevel.STALE -> 0xFFA7B0BA.toInt()
    }

    fun secondaryLabel(): String {
        if (stale) return "stale"
        return "mg/dL ${trendArrow()}"
    }
}

object GlucoseKeys {
    const val PATH_LATEST = "/glucose/latest"

    const val VALUE_MG_DL = "valueMgDl"
    const val TREND = "trend"
    const val DELTA_MG_DL = "deltaMgDl"
    const val TIMESTAMP_EPOCH_MS = "timestampEpochMs"
    const val STALE = "stale"
    const val HISTORY = "history"
}

class GlucoseCache(context: Context) {
    private val prefs = context.getSharedPreferences("glucose_cache", Context.MODE_PRIVATE)

    fun save(snapshot: GlucoseSnapshot) {
        val history = updatedHistory(snapshot)
        prefs.edit()
            .putInt(GlucoseKeys.VALUE_MG_DL, snapshot.valueMgDl)
            .putString(GlucoseKeys.TREND, snapshot.trend)
            .putInt(GlucoseKeys.DELTA_MG_DL, snapshot.deltaMgDl)
            .putLong(GlucoseKeys.TIMESTAMP_EPOCH_MS, snapshot.timestampEpochMs)
            .putBoolean(GlucoseKeys.STALE, snapshot.stale)
            .putString(GlucoseKeys.HISTORY, history.joinToString(","))
            .apply()
    }

    fun load(): GlucoseSnapshot? {
        if (!prefs.contains(GlucoseKeys.TIMESTAMP_EPOCH_MS)) return null

        return GlucoseSnapshot(
            valueMgDl = prefs.getInt(GlucoseKeys.VALUE_MG_DL, 0),
            trend = prefs.getString(GlucoseKeys.TREND, "FLAT").orEmpty(),
            deltaMgDl = prefs.getInt(GlucoseKeys.DELTA_MG_DL, 0),
            timestampEpochMs = prefs.getLong(GlucoseKeys.TIMESTAMP_EPOCH_MS, 0L),
            stale = prefs.getBoolean(GlucoseKeys.STALE, true),
        )
    }

    fun loadHistory(): List<Int> {
        val raw = prefs.getString(GlucoseKeys.HISTORY, "").orEmpty()
        if (raw.isBlank()) return emptyList()

        return raw.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .takeLast(HISTORY_LIMIT)
    }

    private fun updatedHistory(snapshot: GlucoseSnapshot): List<Int> {
        val existing = loadHistory().toMutableList()
        if (existing.lastOrNull() != snapshot.valueMgDl) {
            existing += snapshot.valueMgDl
        }
        return existing.takeLast(HISTORY_LIMIT)
    }

    companion object {
        private const val HISTORY_LIMIT = 12
    }
}
