package com.glucoseforwatch.mobile.watch

import com.glucoseforwatch.mobile.sync.PhoneSyncStateSnapshot
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WatchSyncVerifierAckWaitTest {
    @Test
    fun returns_true_when_ack_already_matches_push_sequence() =
        runBlocking {
            val state = syncState(lastPushSequenceId = 42L, lastAckSequenceId = 42L)

            val acked =
                WatchSyncVerifier.waitForWatchAck(
                    loadState = { state },
                    pushSequenceId = 42L,
                    timeoutMs = 500L,
                )

            assertTrue(acked)
        }

    @Test
    fun returns_true_when_ack_arrives_before_timeout() =
        runBlocking {
            var ackSequenceId = 0L
            val deferred =
                async {
                    delay(100)
                    ackSequenceId = 99L
                }

            val acked =
                WatchSyncVerifier.waitForWatchAck(
                    loadState = {
                        syncState(lastPushSequenceId = 99L, lastAckSequenceId = ackSequenceId)
                    },
                    pushSequenceId = 99L,
                    timeoutMs = 1_000L,
                    pollIntervalMs = 50L,
                )

            deferred.await()
            assertTrue(acked)
        }

    @Test
    fun returns_false_when_ack_never_matches_before_timeout() =
        runBlocking {
            val acked =
                WatchSyncVerifier.waitForWatchAck(
                    loadState = { syncState(lastPushSequenceId = 10L, lastAckSequenceId = 0L) },
                    pushSequenceId = 10L,
                    timeoutMs = 200L,
                    pollIntervalMs = 50L,
                )

            assertFalse(acked)
        }

    private fun syncState(
        lastPushSequenceId: Long,
        lastAckSequenceId: Long,
    ): PhoneSyncStateSnapshot =
        PhoneSyncStateSnapshot(
            lastFetchedReadingTimestampEpochMs = 0L,
            lastPushedReadingTimestampEpochMs = 0L,
            lastPushSequenceId = lastPushSequenceId,
            lastFetchAttemptEpochMs = 0L,
            lastPushSuccessEpochMs = 0L,
            lastPushFailureEpochMs = 0L,
            lastPushFailureMessage = "",
            lastAckReadingTimestampEpochMs = 0L,
            lastAckSequenceId = lastAckSequenceId,
            lastAckEpochMs = 0L,
            lastAckNodeId = "",
            lastPushedValueMgDl = 120,
            lastPushedTrend = "FLAT",
            lastPushedDeltaMgDl = 0,
            lastPushedStale = false,
            unackedRepushCount = 0,
            consecutiveWearPushFailures = 0,
            activeServiceState = "running",
            activeServiceUpdatedAtEpochMs = 0L,
        )
}
