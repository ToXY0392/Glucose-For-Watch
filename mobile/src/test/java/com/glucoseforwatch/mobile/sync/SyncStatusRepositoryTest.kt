package com.glucoseforwatch.mobile.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.glucoseforwatch.core.model.SyncErrorCategory
import com.glucoseforwatch.core.testing.SyncTestFixtures
import com.glucoseforwatch.feature.sync.SyncStatusRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class SyncStatusRepositoryTest {
    private lateinit var context: Context
    private lateinit var repository: SyncStatusRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("widget_g7_sync_status", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = SyncStatusRepository(context)
    }

    @Test
    fun saveFetchedReading_updates_hero_without_resetting_failure_counters() {
        seedPriorFailureState()

        val reading = SyncTestFixtures.glucoseReading(valueMgDl = 142)
        repository.saveFetchedReading("dexcom-share", reading)

        val snapshot = repository.load()
        assertEquals(142, snapshot.lastValueMgDl)
        assertEquals("dexcom-share", snapshot.lastSourceName)
        assertEquals(2, snapshot.consecutiveFailureCount)
        assertEquals("watch offline again", snapshot.lastError)
        assertFalse(snapshot.watchPushPending)
    }

    @Test
    fun saveWatchDeliveryPending_updates_hero_and_marks_watch_push_pending() {
        seedPriorFailureState()

        val reading = SyncTestFixtures.glucoseReading(valueMgDl = 155)
        repository.saveWatchDeliveryPending("dexcom-share", reading)

        val snapshot = repository.load()
        assertEquals(155, snapshot.lastValueMgDl)
        assertTrue(snapshot.watchPushPending)
        assertEquals(2, snapshot.consecutiveFailureCount)
        assertEquals("", snapshot.lastError)
    }

    @Test
    fun saveSuccess_clears_errors_and_watch_push_pending() {
        repository.saveError("network down", SyncErrorCategory.NETWORK)
        repository.saveWatchDeliveryPending(
            "dexcom-share",
            SyncTestFixtures.glucoseReading(valueMgDl = 100),
        )

        repository.saveSuccess(
            "dexcom-share",
            SyncTestFixtures.glucoseReading(valueMgDl = 101),
        )

        val snapshot = repository.load()
        assertEquals(101, snapshot.lastValueMgDl)
        assertEquals("", snapshot.lastError)
        assertEquals(SyncErrorCategory.NONE, snapshot.lastErrorCategory)
        assertEquals(0, snapshot.consecutiveFailureCount)
        assertFalse(snapshot.watchPushPending)
    }

    private fun seedPriorFailureState() {
        repository.saveError("watch offline", SyncErrorCategory.OTHER)
        repository.saveError("watch offline again", SyncErrorCategory.OTHER)
    }
}
