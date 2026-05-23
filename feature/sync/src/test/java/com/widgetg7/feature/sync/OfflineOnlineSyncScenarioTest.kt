package com.widgetg7.feature.sync

import com.widgetg7.core.model.GlucoseReading
import com.widgetg7.core.testing.SyncTestFixtures
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Simulates watch offline → online transitions through [WearSyncPort]. */
class OfflineOnlineSyncScenarioTest {
    @Test
    fun offline_enqueue_then_online_push_clears_pending() = runBlocking {
        val reading = SyncTestFixtures.glucoseReading(timestampEpochMs = 5_000L, valueMgDl = 142)
        val wear = SequentialWearSync(listOf(false, true))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 4_000L)
        val pending = FakePendingPush()

        engine(wear, state, pending, reading).run(triggeredFromWatch = false)
        assertTrue(pending.hasPending())
        assertEquals(0, state.pushSuccessCalls)

        engine(wear, state, pending, reading).run(triggeredFromWatch = false)
        assertFalse(pending.hasPending())
        assertEquals(1, state.pushSuccessCalls)
        assertEquals(2, wear.pushCalls)
    }

    @Test
    fun offline_then_online_with_new_dexcom_reading_still_delivers() = runBlocking {
        val staleReading = SyncTestFixtures.glucoseReading(timestampEpochMs = 4_000L)
        val freshReading = SyncTestFixtures.glucoseReading(timestampEpochMs = 8_000L, valueMgDl = 155)
        val wear = SequentialWearSync(listOf(false, true))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 3_000L)
        val pending = FakePendingPush()
        val source = MutableSource(staleReading)

        GlucoseSyncEngine(
            source = source,
            syncState = state,
            wearSync = wear,
            refreshStatus = FakeRefreshStatus(),
            pendingPush = pending,
        ).run(triggeredFromWatch = false)

        assertTrue(pending.hasPending())

        source.update(freshReading)
        GlucoseSyncEngine(
            source = source,
            syncState = state,
            wearSync = wear,
            refreshStatus = FakeRefreshStatus(),
            pendingPush = pending,
        ).run(triggeredFromWatch = false)

        assertFalse(pending.hasPending())
        assertEquals(1, state.pushSuccessCalls)
        assertEquals(155, state.lastPushedValue)
    }

    private fun engine(
        wear: SequentialWearSync,
        state: FakeSyncState,
        pending: FakePendingPush,
        reading: GlucoseReading,
    ): GlucoseSyncEngine =
        GlucoseSyncEngine(
            source =
                object : GlucoseSourcePort {
                    override val sourceName: String = "scenario-source"
                    override suspend fun latest(): GlucoseReading = reading
                },
            syncState = state,
            wearSync = wear,
            refreshStatus = FakeRefreshStatus(),
            pendingPush = pending,
        )

    private class MutableSource(private var reading: GlucoseReading) : GlucoseSourcePort {
        override val sourceName: String = "scenario-source"

        override suspend fun latest(): GlucoseReading = reading

        fun update(reading: GlucoseReading) {
            this.reading = reading
        }
    }

    private class SequentialWearSync(private val results: List<Boolean>) : WearSyncPort {
        var pushCalls: Int = 0
            private set

        override suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long): Boolean {
            val index = pushCalls.coerceAtMost(results.lastIndex)
            pushCalls++
            return results[index]
        }
    }

    private class FakeSyncState(
        private val lastPushedReadingTimestampEpochMs: Long,
    ) : SyncStatePort {
        var pushSuccessCalls: Int = 0
            private set
        var lastPushedValue: Int = 0
            private set

        override fun load(): SyncStateSnapshot =
            SyncStateSnapshot(lastPushedReadingTimestampEpochMs = lastPushedReadingTimestampEpochMs)

        override fun recordFetchAttempt() = Unit
        override fun recordFetchedReading(timestampEpochMs: Long) = Unit
        override fun nextSequenceId(): Long = 42L

        override fun recordPushSuccess(reading: GlucoseReading, sequenceId: Long) {
            pushSuccessCalls++
            lastPushedValue = reading.valueMgDl
        }
    }

    private class FakePendingPush : PendingPushPort {
        private var pending = false

        override fun hasPending(): Boolean = pending

        override fun enqueue(reading: GlucoseReading) {
            pending = true
        }

        override fun clear() {
            pending = false
        }
    }

    private class FakeRefreshStatus : RefreshStatusPort {
        override suspend fun pushCompletedPhoneUpToDateWatchUnavailable() = Unit
        override suspend fun pushCompletedNoNewReading() = Unit
    }
}
