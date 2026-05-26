package com.widgetg7.wear.sync

/** Connected phone node metadata for sync target selection. */
data class PhoneNode(
    val id: String,
    val displayName: String = "",
    val isNearby: Boolean = false,
)

/**
 * Picks which connected phone should receive a tile refresh message.
 *
 * Priority: last phone that pushed glucose → sole nearby phone → stable displayName sort.
 * Multiple phones without history: first sorted by displayName (documented limitation).
 */
object PhoneTargetResolver {
    fun selectPhoneNodeId(
        connectedNodes: List<PhoneNode>,
        lastPushSourceNodeId: String?,
    ): String? {
        if (connectedNodes.isEmpty()) return null

        lastPushSourceNodeId?.let { lastId ->
            connectedNodes.find { it.id == lastId }?.let { return it.id }
        }

        val nearbyOnly = connectedNodes.filter { it.isNearby }
        if (nearbyOnly.size == 1) {
            return nearbyOnly.first().id
        }

        return connectedNodes
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.displayName.ifBlank { it.id } })
            .first()
            .id
    }
}
