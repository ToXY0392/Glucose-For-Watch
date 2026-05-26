package com.widgetg7.mobile.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.widgetg7.core.model.SyncErrorCategory
import com.widgetg7.core.testing.SyncTestFixtures
import com.widgetg7.mobile.R
import com.widgetg7.mobile.sync.PhoneSyncStateSnapshot
import com.widgetg7.mobile.watch.WatchConnectionStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class MainActivitySyncPillTest {
    private lateinit var labels: HomeSyncPillLabels

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        labels =
            HomeSyncPillLabels(
                dexcomOff = context.getString(R.string.home_status_dexcom_off),
                watchUnreachable = context.getString(R.string.home_status_watch_unreachable),
                watchPushPending = context.getString(R.string.home_status_watch_push_pending),
                watchPending = context.getString(R.string.home_status_watch_pending),
                watchNotPaired = context.getString(R.string.home_status_watch_not_paired),
                watchInstall = context.getString(R.string.home_status_watch_install),
                syncError = { error -> context.getString(R.string.home_status_sync_error, error) },
                watchConfirmed = context.getString(R.string.home_status_watch_confirmed),
                syncActive = context.getString(R.string.home_status_sync_active),
                ready = context.getString(R.string.home_status_ready),
            )
    }

    @Test
    fun E5_watch_push_pending_shows_pending_pill_not_sync_active() {
        val pill =
            HomeSyncPillResolver.resolve(
                dexcomConfigured = true,
                activeSync = true,
                syncStatus = SyncTestFixtures.syncStatusSnapshot(
                    lastValueMgDl = 120,
                    lastSyncEpochMs = 1_000L,
                    watchPushPending = true,
                ),
                watchStatus = connectedWatch(),
                watchReady = true,
                syncState = syncState(),
                watchPushPending = true,
                labels = labels,
            )

        assertEquals(labels.watchPushPending, pill)
    }

    @Test
    fun E5_successful_sync_shows_active_pill() {
        val pill =
            HomeSyncPillResolver.resolve(
                dexcomConfigured = true,
                activeSync = true,
                syncStatus = SyncTestFixtures.syncStatusSnapshot(
                    lastValueMgDl = 120,
                    lastSyncEpochMs = 1_000L,
                    watchPushPending = false,
                ),
                watchStatus = connectedWatch(),
                watchReady = true,
                syncState = syncState(),
                watchPushPending = false,
                labels = labels,
            )

        assertEquals(labels.syncActive, pill)
    }

    @Test
    fun E5_dexcom_off_shows_connect_pill() {
        val pill =
            HomeSyncPillResolver.resolve(
                dexcomConfigured = false,
                activeSync = false,
                syncStatus = SyncTestFixtures.syncStatusSnapshot(lastValueMgDl = null),
                watchStatus = connectedWatch(),
                watchReady = true,
                syncState = syncState(),
                watchPushPending = false,
                labels = labels,
            )

        assertEquals(labels.dexcomOff, pill)
    }

    private fun connectedWatch(): WatchConnectionStatus =
        WatchConnectionStatus(
            connected = true,
            nodeId = "watch-1",
            displayName = "Galaxy Watch",
            connectedWatches = emptyList(),
            preferredNodeId = "watch-1",
            preferredNodeMissing = false,
        )

    private fun syncState(
        consecutiveWearPushFailures: Int = 0,
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
            lastPushedStale = true,
            unackedRepushCount = 0,
            consecutiveWearPushFailures = consecutiveWearPushFailures,
            activeServiceState = "stopped",
            activeServiceUpdatedAtEpochMs = 0L,
        )
}
