package com.glucoseforwatch.wear.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PhoneTargetResolverTest {
    @Test
    fun E3_multi_node_prefers_last_push_source() {
        val nodes = listOf(
            PhoneNode(id = "node-a", displayName = "A"),
            PhoneNode(id = "node-b", displayName = "B"),
        )

        assertEquals("node-a", PhoneTargetResolver.selectPhoneNodeId(nodes, "node-a"))
    }

    @Test
    fun A2_T2_without_history_uses_stable_display_name_sort() {
        val nodes = listOf(
            PhoneNode(id = "node-b", displayName = "B"),
            PhoneNode(id = "node-a", displayName = "A"),
        )

        assertEquals("node-a", PhoneTargetResolver.selectPhoneNodeId(nodes, null))
    }

    @Test
    fun A2_T3_empty_nodes_returns_null() {
        assertNull(PhoneTargetResolver.selectPhoneNodeId(emptyList(), "node-a"))
    }

    @Test
    fun sole_nearby_phone_is_selected_without_history() {
        val nodes = listOf(
            PhoneNode(id = "node-far", displayName = "Far", isNearby = false),
            PhoneNode(id = "node-near", displayName = "Near", isNearby = true),
        )

        assertEquals("node-near", PhoneTargetResolver.selectPhoneNodeId(nodes, null))
    }
}
