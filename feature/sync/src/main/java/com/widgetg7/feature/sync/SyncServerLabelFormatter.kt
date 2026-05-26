package com.widgetg7.feature.sync

/** Short Dexcom region label for UI ("US" or "Europe"). */
object SyncServerLabelFormatter {
    fun displayServer(server: String): String = if (server.equals("US", true)) "US" else "Europe"
}
