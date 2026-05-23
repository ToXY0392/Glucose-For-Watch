package com.widgetg7.mobile.sync

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class WatchReconnectDetectorTest {
    @Test
    fun reconnect_callback_fires_after_offline_to_online_transition() = runBlocking {
        var connected = false
        var flushCalls = 0
        val detector = WatchReconnectDetector { connected }

        detector.onBeforeSyncPass { flushCalls++ }
        assertEquals(0, flushCalls)

        detector.onBeforeSyncPass { flushCalls++ }
        assertEquals(0, flushCalls)

        connected = true
        detector.onBeforeSyncPass { flushCalls++ }
        assertEquals(1, flushCalls)
    }

    @Test
    fun reconnect_not_fired_when_watch_stays_online() = runBlocking {
        var flushCalls = 0
        val detector = WatchReconnectDetector { true }

        repeat(3) {
            detector.onBeforeSyncPass { flushCalls++ }
        }
        assertEquals(0, flushCalls)
    }
}
