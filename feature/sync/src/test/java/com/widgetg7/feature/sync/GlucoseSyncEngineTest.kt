package com.widgetg7.feature.sync

import com.widgetg7.core.model.GlucoseReading
import com.widgetg7.core.testing.SyncTestFixtures
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlucoseSyncEngineTest {
    @Test
    fun triggered_from_watch_without_new_reading_pushes_no_new_reading_status() = runBlocking {
        val source = FakeSource(reading(timestampEpochMs = 1000L))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 1000L)
        val wear = FakeWearSync(pushResult = true)
        val refresh = FakeRefreshStatus()

        val result = engine(source, state, wear, refresh).run(triggeredFromWatch = true)

        assertTrue(result is SyncExecutionResult.SuccessNoNewReading)
        assertEquals(1, refresh.noNewReadingCount)
        assertEquals(0, refresh.watchUnavailableCount)
        assertEquals(0, state.pushSuccessCalls)
    }

    @Test
    fun triggered_from_watch_and_push_fails_notifies_watch_unavailable() = runBlocking {
        val source = FakeSource(reading(timestampEpochMs = 2000L))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 1000L)
        val wear = FakeWearSync(pushResult = false)
        val refresh = FakeRefreshStatus()

        val result = engine(source, state, wear, refresh).run(triggeredFromWatch = true)

        assertTrue(result is SyncExecutionResult.SuccessNewReading)
        assertEquals(0, refresh.noNewReadingCount)
        assertEquals(1, refresh.watchUnavailableCount)
        assertEquals(0, state.pushSuccessCalls)
    }

    @Test
    fun new_reading_and_push_success_records_push_success() = runBlocking {
        val source = FakeSource(reading(timestampEpochMs = 3000L))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 1000L)
        val wear = FakeWearSync(pushResult = true)
        val refresh = FakeRefreshStatus()

        val result = engine(source, state, wear, refresh).run(triggeredFromWatch = false)

        assertTrue(result is SyncExecutionResult.SuccessNewReading)
        assertEquals(1, state.pushSuccessCalls)
        assertEquals(0, refresh.noNewReadingCount)
        assertEquals(0, refresh.watchUnavailableCount)
    }

    private fun engine(
        source: FakeSource,
        syncState: FakeSyncState,
        wearSync: FakeWearSync,
        refresh: FakeRefreshStatus,
    ): GlucoseSyncEngine = GlucoseSyncEngine(source, syncState, wearSync, refresh)

    private class FakeSource(private val reading: GlucoseReading) : GlucoseSourcePort {
        override val sourceName: String = "fake-source"
        override suspend fun latest(): GlucoseReading = reading
    }

    private class FakeSyncState(
        private val lastPushedReadingTimestampEpochMs: Long,
    ) : SyncStatePort {
        var pushSuccessCalls: Int = 0
            private set

        override fun load(): SyncStateSnapshot =
            SyncStateSnapshot(lastPushedReadingTimestampEpochMs = lastPushedReadingTimestampEpochMs)

        override fun recordFetchAttempt() = Unit
        override fun recordFetchedReading(timestampEpochMs: Long) = Unit
        override fun nextSequenceId(): Long = 42L
        override fun recordPushSuccess(reading: GlucoseReading, sequenceId: Long) {
            pushSuccessCalls++
        }
    }

    private class FakeWearSync(private val pushResult: Boolean) : WearSyncPort {
        override suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long): Boolean = pushResult
    }

    private class FakeRefreshStatus : RefreshStatusPort {
        var watchUnavailableCount: Int = 0
            private set
        var noNewReadingCount: Int = 0
            private set

        override suspend fun pushCompletedPhoneUpToDateWatchUnavailable() {
            watchUnavailableCount++
        }

        override suspend fun pushCompletedNoNewReading() {
            noNewReadingCount++
        }
    }

    private fun reading(timestampEpochMs: Long) =
        SyncTestFixtures.glucoseReading(
            timestampEpochMs = timestampEpochMs,
        )
}
