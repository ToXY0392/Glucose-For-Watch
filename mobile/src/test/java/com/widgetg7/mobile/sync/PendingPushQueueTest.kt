package com.widgetg7.mobile.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.widgetg7.core.testing.SyncTestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class PendingPushQueueTest {
    private lateinit var context: Context
    private lateinit var queue: PendingPushQueue

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("widget_g7_pending_push_queue", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        queue = PendingPushQueue(context)
    }

    @Test
    fun starts_empty() {
        assertFalse(queue.hasPending())
        assertNull(queue.loadReading())
    }

    @Test
    fun enqueue_persists_reading_fields() {
        val reading = SyncTestFixtures.glucoseReading(
            valueMgDl = 118,
            trend = "UP",
            deltaMgDl = 3,
            timestampEpochMs = 9_000L,
            stale = false,
        )

        queue.enqueue(reading)

        assertTrue(queue.hasPending())
        val loaded = queue.loadReading()
        requireNotNull(loaded)
        assertEquals(118, loaded.valueMgDl)
        assertEquals("UP", loaded.trend)
        assertEquals(3, loaded.deltaMgDl)
        assertEquals(9_000L, loaded.timestampEpochMs)
        assertFalse(loaded.stale)
    }

    @Test
    fun clear_removes_pending_reading() {
        queue.enqueue(SyncTestFixtures.glucoseReading())
        queue.clear()
        assertFalse(queue.hasPending())
        assertNull(queue.loadReading())
    }
}
