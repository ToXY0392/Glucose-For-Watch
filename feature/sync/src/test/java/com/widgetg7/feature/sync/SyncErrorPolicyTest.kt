package com.widgetg7.feature.sync

import com.widgetg7.core.model.SyncErrorCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncErrorPolicyTest {
    @Test
    fun timeout_hint_maps_to_network_category() {
        assertEquals(
            SyncErrorCategory.NETWORK,
            SyncErrorPolicy.fromTimeoutHint("operation timed out"),
        )
    }

    @Test
    fun non_timeout_hint_returns_null() {
        assertEquals(
            null,
            SyncErrorPolicy.fromTimeoutHint("connection refused"),
        )
    }

    @Test
    fun dexcom_kind_mapping_matches_contract() {
        assertEquals(SyncErrorCategory.AUTH, SyncErrorPolicy.fromDexcomKind("AUTH"))
        assertEquals(SyncErrorCategory.NETWORK, SyncErrorPolicy.fromDexcomKind("SESSION"))
        assertEquals(SyncErrorCategory.NETWORK, SyncErrorPolicy.fromDexcomKind("NETWORK"))
        assertEquals(SyncErrorCategory.OTHER, SyncErrorPolicy.fromDexcomKind("NO_DATA"))
        assertEquals(SyncErrorCategory.OTHER, SyncErrorPolicy.fromDexcomKind(null))
    }
}
