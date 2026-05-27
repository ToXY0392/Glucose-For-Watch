package com.glucoseforwatch.feature.sync

import com.glucoseforwatch.core.model.GlucoseReading
import com.glucoseforwatch.core.testing.SyncTestFixtures
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlucoseSyncEngineTest {
    @Test
    fun E2_all_watch_delivery_statuses_are_reachable() = runBlocking {
        val reading = SyncTestFixtures.glucoseReading(timestampEpochMs = 2_000L)
        val statuses =
            mapOf(
                WatchDeliveryStatus.DELIVERED to
                    engine(
                        FakeWearSync(true),
                        FakeSyncState(1_000L),
                        FakePendingPush(),
                        reading,
                    ).run(triggeredFromWatch = false).watchDelivery,
                WatchDeliveryStatus.QUEUED to
                    engine(
                        FakeWearSync(false),
                        FakeSyncState(1_000L),
                        FakePendingPush(),
                        reading,
                    ).run(triggeredFromWatch = false).watchDelivery,
                WatchDeliveryStatus.WATCH_UNAVAILABLE to
                    GlucoseSyncEngine(
                        source = object : GlucoseSourcePort {
                            override val sourceName = "fake"
                            override suspend fun latest() = reading
                        },
                        syncState = FakeSyncState(1_000L),
                        wearSync = FakeWearSync(false),
                        refreshStatus = FakeRefreshStatus(),
                        pendingPush = null,
                    ).run(triggeredFromWatch = false).watchDelivery,
                WatchDeliveryStatus.NOT_APPLICABLE to
                    GlucoseSyncEngine(
                        source = object : GlucoseSourcePort {
                            override val sourceName = "fake"
                            override suspend fun latest() =
                                SyncTestFixtures.glucoseReading(timestampEpochMs = 1_000L)
                        },
                        syncState = FakeSyncState(1_000L),
                        wearSync = FakeWearSync(true),
                        refreshStatus = FakeRefreshStatus(),
                    ).run(triggeredFromWatch = true).watchDelivery,
            )

        assertEquals(WatchDeliveryStatus.entries.toSet(), statuses.values.toSet())
    }

    @Test
    fun A4_T3_triggered_from_watch_without_new_reading_is_not_applicable() = runBlocking {
        val source = FakeSource(reading(timestampEpochMs = 1000L))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 1000L)
        val wear = FakeWearSync(pushResult = true)
        val refresh = FakeRefreshStatus()

        val result = engine(source, state, wear, refresh).run(triggeredFromWatch = true)

        assertTrue(result is SyncExecutionResult.SuccessNoNewReading)
        assertEquals(WatchDeliveryStatus.NOT_APPLICABLE, result.watchDelivery)
        assertEquals(1, refresh.noNewReadingCount)
        assertEquals(0, refresh.watchUnavailableCount)
        assertEquals(0, state.pushSuccessCalls)
    }

    @Test
    fun A4_T5_triggered_from_watch_and_push_fails_queues_and_notifies_watch() = runBlocking {
        val source = FakeSource(reading(timestampEpochMs = 2000L))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 1000L)
        val wear = FakeWearSync(pushResult = false)
        val refresh = FakeRefreshStatus()
        val pending = FakePendingPush()

        val result = engine(source, state, wear, refresh, pending).run(triggeredFromWatch = true)

        assertTrue(result is SyncExecutionResult.SuccessNewReading)
        assertEquals(WatchDeliveryStatus.QUEUED, result.watchDelivery)
        assertEquals(0, refresh.noNewReadingCount)
        assertEquals(1, refresh.watchUnavailableCount)
        assertEquals(0, state.pushSuccessCalls)
        assertEquals(1, pending.enqueueCount)
    }

    @Test
    fun A4_T1_new_reading_and_push_success_delivers_to_watch() = runBlocking {
        val source = FakeSource(reading(timestampEpochMs = 3000L))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 1000L)
        val wear = FakeWearSync(pushResult = true)
        val refresh = FakeRefreshStatus()

        val result = engine(source, state, wear, refresh).run(triggeredFromWatch = false)

        assertTrue(result is SyncExecutionResult.SuccessNewReading)
        assertEquals(WatchDeliveryStatus.DELIVERED, result.watchDelivery)
        assertEquals(1, state.pushSuccessCalls)
        assertEquals(0, refresh.noNewReadingCount)
        assertEquals(0, refresh.watchUnavailableCount)
    }

    @Test
    fun A4_T2_push_failure_enqueues_pending_reading() = runBlocking {
        val source = FakeSource(reading(timestampEpochMs = 2000L))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 1000L)
        val wear = FakeWearSync(pushResult = false)
        val refresh = FakeRefreshStatus()
        val pending = FakePendingPush()

        val result = engine(source, state, wear, refresh, pending).run(triggeredFromWatch = false)

        assertTrue(result is SyncExecutionResult.SuccessNewReading)
        assertEquals(WatchDeliveryStatus.QUEUED, result.watchDelivery)
        assertEquals(1, pending.enqueueCount)
        assertEquals(0, state.pushSuccessCalls)
    }

    @Test
    fun push_failure_without_pending_port_is_watch_unavailable() = runBlocking {
        val source = FakeSource(reading(timestampEpochMs = 2000L))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 1000L)
        val wear = FakeWearSync(pushResult = false)
        val refresh = FakeRefreshStatus()

        val result = engine(source, state, wear, refresh).run(triggeredFromWatch = false)

        assertTrue(result is SyncExecutionResult.SuccessNewReading)
        assertEquals(WatchDeliveryStatus.WATCH_UNAVAILABLE, result.watchDelivery)
        assertEquals(0, state.pushSuccessCalls)
    }

    @Test
    fun A4_T4_pending_push_retries_when_timestamp_unchanged() = runBlocking {
        val source = FakeSource(reading(timestampEpochMs = 1000L))
        val state = FakeSyncState(lastPushedReadingTimestampEpochMs = 1000L)
        val wear = FakeWearSync(pushResult = true)
        val refresh = FakeRefreshStatus()
        val pending = FakePendingPush(initiallyPending = true)

        val result = engine(source, state, wear, refresh, pending).run(triggeredFromWatch = false)

        assertTrue(result is SyncExecutionResult.SuccessNoNewReading)
        assertEquals(WatchDeliveryStatus.DELIVERED, result.watchDelivery)
        assertEquals(1, wear.pushCalls)
        assertEquals(1, state.pushSuccessCalls)
        assertEquals(1, pending.clearCount)
    }

    private fun engine(
        wearSync: FakeWearSync,
        syncState: FakeSyncState,
        pending: FakePendingPush,
        reading: GlucoseReading,
    ): GlucoseSyncEngine =
        GlucoseSyncEngine(
            source =
                object : GlucoseSourcePort {
                    override val sourceName: String = "fake-source"
                    override suspend fun latest(): GlucoseReading = reading
                },
            syncState = syncState,
            wearSync = wearSync,
            refreshStatus = FakeRefreshStatus(),
            pendingPush = pending,
        )

    private fun engine(
        source: FakeSource,
        syncState: FakeSyncState,
        wearSync: FakeWearSync,
        refresh: FakeRefreshStatus,
        pending: FakePendingPush? = null,
    ): GlucoseSyncEngine = GlucoseSyncEngine(source, syncState, wearSync, refresh, pending)

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
        var pushCalls: Int = 0
            private set

        override suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long): Boolean {
            pushCalls++
            return pushResult
        }
    }

    private class FakePendingPush(
        private val initiallyPending: Boolean = false,
    ) : PendingPushPort {
        var enqueueCount: Int = 0
            private set
        var clearCount: Int = 0
            private set
        private var pending = initiallyPending

        override fun hasPending(): Boolean = pending

        override fun enqueue(reading: GlucoseReading) {
            enqueueCount++
            pending = true
        }

        override fun clear() {
            clearCount++
            pending = false
        }
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
