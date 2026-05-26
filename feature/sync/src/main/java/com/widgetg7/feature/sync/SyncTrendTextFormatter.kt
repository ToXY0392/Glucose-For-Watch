package com.widgetg7.feature.sync

/** Maps Dexcom trend tokens to French display labels. */
object SyncTrendTextFormatter {
    fun displayTrend(trend: String): String = when (trend) {
        "UP" -> "en hausse"
        "UP_RIGHT" -> "en hausse légère"
        "FLAT" -> "stable"
        "DOWN_RIGHT" -> "en baisse légère"
        "DOWN" -> "en baisse"
        else -> trend
    }
}
