package com.widgetg7.mobile.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.widgetg7.core.model.SyncErrorCategory
import com.widgetg7.core.testing.SyncTestFixtures
import com.widgetg7.feature.sync.SyncExecutionResult
import com.widgetg7.feature.sync.SyncStatusRepository
import com.widgetg7.feature.sync.WatchDeliveryStatus
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
class PhoneGlucoseSyncEngineTest {
    private lateinit var repository: SyncStatusRepository
    private val reading = SyncTestFixtures.glucoseReading(valueMgDl = 118)

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("widget_g7_sync_status", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = SyncStatusRepository(context)
    }

    @Test
    fun E1_push_fail_does_not_reset_consecutive_failure_count() {
        repository.saveError("network", SyncErrorCategory.NETWORK)

        PhoneSyncStatusPersister.persist(
            repository = repository,
            result = SyncExecutionResult.SuccessNewReading(
                sourceName = "dexcom-share",
                watchDelivery = WatchDeliveryStatus.QUEUED,
            ),
            reading = reading,
        )

        assertEquals(1, repository.load().consecutiveFailureCount)
        assertTrue(repository.load().watchPushPending)
    }

    @Test
    fun A1_T1_push_fail_does_not_call_full_success() {
        PhoneSyncStatusPersister.persist(
            repository = repository,
            result = SyncExecutionResult.SuccessNewReading(
                sourceName = "dexcom-share",
                watchDelivery = WatchDeliveryStatus.QUEUED,
            ),
            reading = reading,
        )

        val snapshot = repository.load()
        assertEquals(118, snapshot.lastValueMgDl)
        assertTrue(snapshot.watchPushPending)
        assertFalse(snapshot.hasSuccessfulSync() && !snapshot.watchPushPending)
    }

    @Test
    fun A1_T2_push_ok_calls_save_success() {
        PhoneSyncStatusPersister.persist(
            repository = repository,
            result = SyncExecutionResult.SuccessNewReading(
                sourceName = "dexcom-share",
                watchDelivery = WatchDeliveryStatus.DELIVERED,
            ),
            reading = reading,
        )

        val snapshot = repository.load()
        assertEquals(118, snapshot.lastValueMgDl)
        assertFalse(snapshot.watchPushPending)
        assertTrue(snapshot.hasSuccessfulSync())
    }

    @Test
    fun A1_T3_push_fail_keeps_hero_but_blocks_sync_active_pill_state() {
        PhoneSyncStatusPersister.persist(
            repository = repository,
            result = SyncExecutionResult.SuccessNewReading(
                sourceName = "dexcom-share",
                watchDelivery = WatchDeliveryStatus.WATCH_UNAVAILABLE,
            ),
            reading = reading,
        )

        val snapshot = repository.load()
        assertEquals(118, snapshot.lastValueMgDl)
        assertTrue(snapshot.watchPushPending)
        assertFalse(snapshot.hasSuccessfulSync() && !snapshot.watchPushPending)
    }
}
