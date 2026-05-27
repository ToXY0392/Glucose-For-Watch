package com.glucoseforwatch.feature.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncErrorMessagePolicyTest {
    @Test
    fun timeout_message_is_normalized_for_user() {
        val userMessage = SyncErrorMessagePolicy.toUserMessage("request timed out while reading Dexcom")
        assertEquals(SyncMessageCatalog.SYNC_TIMEOUT, userMessage)
    }

    @Test
    fun blank_message_uses_default_fallback() {
        val userMessage = SyncErrorMessagePolicy.toUserMessage("")
        assertEquals(SyncMessageCatalog.UNKNOWN_ERROR, userMessage)
    }

    @Test
    fun blank_message_uses_custom_fallback() {
        val userMessage = SyncErrorMessagePolicy.toUserMessage("  ", fallback = "fallback custom")
        assertEquals("fallback custom", userMessage)
    }

    @Test
    fun non_timeout_message_is_preserved() {
        val userMessage = SyncErrorMessagePolicy.toUserMessage("service unavailable")
        assertEquals("service unavailable", userMessage)
    }
}
