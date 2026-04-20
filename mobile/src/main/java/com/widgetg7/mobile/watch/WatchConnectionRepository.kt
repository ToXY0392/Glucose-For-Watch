package com.widgetg7.mobile.watch

import android.content.Context
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

data class WatchConnectionStatus(
    val connected: Boolean,
    val displayName: String,
) {
    fun label(): String = if (connected) "Montre connectée : $displayName" else "Aucune montre détectée"
}

class WatchConnectionRepository(private val context: Context) {
    suspend fun loadStatus(): WatchConnectionStatus {
        val nodes = Wearable.getNodeClient(context).connectedNodes.await()
        val node = nodes.firstOrNull()
        return if (node == null) {
            WatchConnectionStatus(connected = false, displayName = "")
        } else {
            WatchConnectionStatus(connected = true, displayName = node.displayName)
        }
    }
}
