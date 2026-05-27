package com.glucoseforwatch.wear.sync

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WearAckPolicyTest {
    @Test
    fun E4_succeeds_on_first_attempt() = runBlocking {
        var attempts = 0

        val sent = WearAckSender(maxAttempts = 3, retryDelayMs = 1L, delayFn = {}).send {
            attempts += 1
        }

        assertTrue(sent)
        assertEquals(1, attempts)
    }

    @Test
    fun E4_retries_until_success() = runBlocking {
        var attempts = 0
        val delays = mutableListOf<Long>()

        val sent = WearAckSender(
            maxAttempts = 3,
            retryDelayMs = 100L,
            delayFn = { delays += it },
        ).send {
            attempts += 1
            if (attempts < 3) error("transient")
        }

        assertTrue(sent)
        assertEquals(3, attempts)
        assertEquals(listOf(100L, 200L), delays)
    }

    @Test
    fun E4_returns_false_after_max_attempts() = runBlocking {
        var attempts = 0

        val sent = WearAckSender(maxAttempts = 3, retryDelayMs = 1L, delayFn = {}).send {
            attempts += 1
            error("always fails")
        }

        assertFalse(sent)
        assertEquals(WearAckSender.MAX_ACK_ATTEMPTS, attempts)
    }
}
