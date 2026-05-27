package com.glucoseforwatch.mobile.watch

import android.content.Context
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

/** Wear OS node id and display name for a connected watch. */
data class ConnectedWatchNode(
    val nodeId: String,
    val displayName: String,
)

/** Resolved watch connection state including preferred watch selection. */
data class WatchConnectionStatus(
    val connected: Boolean,
    val nodeId: String,
    val displayName: String,
    val connectedWatches: List<ConnectedWatchNode>,
    val preferredNodeId: String,
    val preferredNodeMissing: Boolean,
) {
    fun label(): String = when {
        !connected -> "Aucune montre détectée"
        connectedWatches.size > 1 -> "Montre principale connectée : $displayName"
        else -> "Montre connectée : $displayName"
    }
}

/** Loads connected watches and persists the user's preferred watch. */
class WatchConnectionRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun loadConnectedWatches(): List<ConnectedWatchNode> =
        Wearable.getNodeClient(context).connectedNodes.await()
            .map { ConnectedWatchNode(nodeId = it.id, displayName = it.displayName) }
            .sortedBy { it.displayName.lowercase() }

    suspend fun loadStatus(): WatchConnectionStatus {
        val watches = loadConnectedWatches()
        val preferredNodeId = loadPreferredWatchId()
        val preferredWatch = watches.firstOrNull { it.nodeId == preferredNodeId }
        val selectedWatch = preferredWatch ?: watches.firstOrNull()

        return if (selectedWatch == null) {
            WatchConnectionStatus(
                connected = false,
                nodeId = "",
                displayName = "",
                connectedWatches = emptyList(),
                preferredNodeId = preferredNodeId,
                preferredNodeMissing = preferredNodeId.isNotBlank(),
            )
        } else {
            WatchConnectionStatus(
                connected = true,
                nodeId = selectedWatch.nodeId,
                displayName = selectedWatch.displayName,
                connectedWatches = watches,
                preferredNodeId = preferredNodeId,
                preferredNodeMissing = preferredNodeId.isNotBlank() && preferredWatch == null,
            )
        }
    }

    fun savePreferredWatch(watch: ConnectedWatchNode) {
        prefs.edit()
            .putString(KEY_PREFERRED_WATCH_ID, watch.nodeId)
            .putString(KEY_PREFERRED_WATCH_NAME, watch.displayName)
            .apply()
    }

    fun clearPreferredWatch() {
        prefs.edit()
            .remove(KEY_PREFERRED_WATCH_ID)
            .remove(KEY_PREFERRED_WATCH_NAME)
            .apply()
    }

    fun loadPreferredWatchId(): String = prefs.getString(KEY_PREFERRED_WATCH_ID, "").orEmpty()

    companion object {
        private const val PREFS_NAME = "widget_g7_watch_connection"
        private const val KEY_PREFERRED_WATCH_ID = "preferred_watch_id"
        private const val KEY_PREFERRED_WATCH_NAME = "preferred_watch_name"
    }
}
