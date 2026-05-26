package com.widgetg7.mobile.watch

import androidx.test.core.app.ApplicationProvider
import com.widgetg7.mobile.R
import com.widgetg7.mobile.sync.PhoneSyncStateSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class WatchHomeCardSummaryTest {
    @Test
    fun offline_watch_shows_red_state() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val state =
            WatchHomeCardSummary.resolve(
                context = context,
                watchStatus =
                    WatchConnectionStatus(
                        connected = false,
                        nodeId = "",
                        displayName = "",
                        connectedWatches = emptyList(),
                        preferredNodeId = "",
                        preferredNodeMissing = false,
                    ),
                watchHealth = null,
                syncState = emptySyncState(),
            )

        assertFalse(state.linked)
        assertEquals(context.getString(R.string.home_watch_status_off), state.subtitle)
    }

    @Test
    fun linked_watch_with_ack_shows_ok_line() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val state =
            WatchHomeCardSummary.resolve(
                context = context,
                watchStatus =
                    WatchConnectionStatus(
                        connected = true,
                        nodeId = "abc",
                        displayName = "Pixel Watch 2",
                        connectedWatches = emptyList(),
                        preferredNodeId = "abc",
                        preferredNodeMissing = false,
                    ),
                watchHealth =
                    WatchSyncHealthStatus(
                        batteryLevel = 80,
                        isCharging = false,
                        lowPowerMode = false,
                        syncLimited = false,
                        message = "",
                        updatedAtEpochMs = System.currentTimeMillis(),
                        manufacturer = "Google",
                        model = "Pixel Watch 2",
                        device = "aurora",
                        appInstalled = true,
                        appVersionName = "0.4.0",
                        appVersionCode = 1L,
                        supportsTile = true,
                        supportsComplication = true,
                    ),
                syncState = emptySyncState(lastPushSequenceId = 3L, lastAckSequenceId = 3L),
            )

        assertTrue(state.linked)
        assertEquals(
            context.getString(R.string.home_watch_status_ok, "Pixel Watch 2"),
            state.subtitle,
        )
    }

    private fun emptySyncState(
        lastPushSequenceId: Long = 0L,
        lastAckSequenceId: Long = 0L,
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
            lastPushedValueMgDl = null,
            lastPushedTrend = "",
            lastPushedDeltaMgDl = 0,
            lastPushedStale = false,
            unackedRepushCount = 0,
            consecutiveWearPushFailures = 0,
            activeServiceState = "",
            activeServiceUpdatedAtEpochMs = 0L,
        )
}
