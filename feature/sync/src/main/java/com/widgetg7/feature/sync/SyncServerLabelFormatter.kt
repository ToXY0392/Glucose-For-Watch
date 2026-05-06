package com.widgetg7.feature.sync

object SyncServerLabelFormatter {
    fun displayServer(server: String): String = if (server.equals("US", true)) "US" else "Europe"
}
