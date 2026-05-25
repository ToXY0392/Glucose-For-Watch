package com.widgetg7.mobile.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.widgetg7.core.model.AgpGlucoseColors
import com.widgetg7.core.testing.SyncTestFixtures
import com.widgetg7.mobile.R
import com.widgetg7.mobile.settings.DexcomUserSettings
import com.widgetg7.mobile.sync.PhoneSyncStateSnapshot
import com.widgetg7.mobile.watch.WatchConnectionStatus
import com.widgetg7.mobile.watch.WatchSyncHealthStatus
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
class HomeStateMapperTest {
    private lateinit var context: Context
    private val nowEpochMs = 1_700_000_000_000L

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun M1_reading_state_shows_value_and_rows() {
        val state =
            HomeStateMapper.map(
                context = context,
                dexcomSettings = DexcomUserSettings("alice", "secret", "OUS"),
                syncStatus =
                    SyncTestFixtures.syncStatusSnapshot(
                        lastValueMgDl = 120,
                        lastReadingTimestampEpochMs = nowEpochMs - 120_000L,
                        lastSyncEpochMs = nowEpochMs - 300_000L,
                        lastTrend = "FLAT",
                    ),
                watchStatus = connectedWatch(),
                watchHealth = installedWatchHealth(),
                syncState = ackedSyncState(),
                batteryProtected = true,
                watchPushPending = false,
                activeSyncEnabled = true,
                nowEpochMs = nowEpochMs,
            )

        assertEquals("120", state.watchFaceValueText)
        assertTrue(state.watchFaceMetaVisible)
        assertEquals(context.getString(R.string.home_dexcom_status_on), state.dexcomRowStatus.substringBefore(" ·"))
        assertFalse(state.showInstallRow)
        assertTrue(state.syncStatusLineVisible)
        assertEquals(context.getString(R.string.home_status_watch_confirmed), state.syncStatusLine)
    }

    @Test
    fun B2_sync_error_shows_error_status_line() {
        val state =
            HomeStateMapper.map(
                context = context,
                dexcomSettings = DexcomUserSettings("alice", "secret", "OUS"),
                syncStatus =
                    SyncTestFixtures.syncStatusSnapshot(
                        lastValueMgDl = 120,
                        lastError = "Session Dexcom expirée",
                    ),
                watchStatus = connectedWatch(),
                watchHealth = installedWatchHealth(),
                syncState = ackedSyncState(),
                batteryProtected = true,
                watchPushPending = false,
                activeSyncEnabled = true,
                nowEpochMs = nowEpochMs,
            )

        assertTrue(state.syncStatusLine.contains("Session Dexcom"))
        assertEquals(R.color.wg7_danger, state.syncStatusLineTextColorRes)
    }

    @Test
    fun B2_watch_push_pending_shows_warn_status_line() {
        val state =
            HomeStateMapper.map(
                context = context,
                dexcomSettings = DexcomUserSettings("alice", "secret", "OUS"),
                syncStatus =
                    SyncTestFixtures.syncStatusSnapshot(
                        lastValueMgDl = 120,
                        watchPushPending = true,
                    ),
                watchStatus = connectedWatch(),
                watchHealth = installedWatchHealth(),
                syncState = emptySyncState(),
                batteryProtected = true,
                watchPushPending = true,
                activeSyncEnabled = true,
                nowEpochMs = nowEpochMs,
            )

        assertEquals(context.getString(R.string.home_status_watch_push_pending), state.syncStatusLine)
        assertEquals(R.color.wg7_accent_dark, state.syncStatusLineTextColorRes)
    }

    @Test
    fun M2_companion_sync_age_uses_reading_timestamp_not_sync_clock() {
        val readingTs = nowEpochMs - 5 * 60_000L
        val syncStatus =
            SyncTestFixtures.syncStatusSnapshot(
                lastValueMgDl = 120,
                lastReadingTimestampEpochMs = readingTs,
                lastSyncEpochMs = nowEpochMs - 60_000L,
            )

        val state =
            HomeStateMapper.map(
                context = context,
                dexcomSettings = DexcomUserSettings("alice", "secret", "OUS"),
                syncStatus = syncStatus,
                watchStatus = connectedWatch(),
                watchHealth = installedWatchHealth(),
                syncState = ackedSyncState(),
                batteryProtected = true,
                watchPushPending = false,
                activeSyncEnabled = true,
                nowEpochMs = nowEpochMs,
            )

        assertTrue(state.syncAgeLabel.contains("min"))
        assertTrue(state.watchFaceMetaText.contains("min"))
    }

    @Test
    fun M1_dexcom_off_shows_connect_hero() {
        val state =
            HomeStateMapper.map(
                context = context,
                dexcomSettings = DexcomUserSettings("", "", "OUS"),
                syncStatus = SyncTestFixtures.syncStatusSnapshot(lastValueMgDl = null),
                watchStatus = connectedWatch(),
                watchHealth = installedWatchHealth(),
                syncState = emptySyncState(),
                batteryProtected = false,
                watchPushPending = false,
                activeSyncEnabled = false,
                nowEpochMs = nowEpochMs,
            )

        assertEquals(context.getString(R.string.home_hero_connect), state.watchFaceMetaText)
        assertFalse(state.syncStatusLineVisible)
        assertEquals(R.color.wg7_text_tertiary, state.syncButtonTintColorRes)
    }

    private fun connectedWatch(): WatchConnectionStatus =
        WatchConnectionStatus(
            connected = true,
            nodeId = "watch-1",
            displayName = "Pixel Watch 2",
            connectedWatches = emptyList(),
            preferredNodeId = "watch-1",
            preferredNodeMissing = false,
        )

    private fun installedWatchHealth(): WatchSyncHealthStatus =
        WatchSyncHealthStatus(
            batteryLevel = 86,
            isCharging = false,
            lowPowerMode = false,
            syncLimited = false,
            message = "",
            updatedAtEpochMs = nowEpochMs,
            manufacturer = "Google",
            model = "Google Pixel Watch 2",
            device = "aurora",
            appInstalled = true,
            appVersionName = "0.4.0",
            appVersionCode = 23L,
            supportsTile = true,
            supportsComplication = true,
        )

    private fun ackedSyncState(): PhoneSyncStateSnapshot =
        emptySyncState().copy(
            lastPushSequenceId = 42L,
            lastAckSequenceId = 42L,
        )

    private fun emptySyncState(): PhoneSyncStateSnapshot =
        PhoneSyncStateSnapshot(
            lastFetchedReadingTimestampEpochMs = 0L,
            lastPushedReadingTimestampEpochMs = 0L,
            lastPushSequenceId = 0L,
            lastFetchAttemptEpochMs = 0L,
            lastPushSuccessEpochMs = 0L,
            lastPushFailureEpochMs = 0L,
            lastPushFailureMessage = "",
            lastAckReadingTimestampEpochMs = 0L,
            lastAckSequenceId = 0L,
            lastAckEpochMs = 0L,
            lastAckNodeId = "",
            lastPushedValueMgDl = null,
            lastPushedTrend = "",
            lastPushedDeltaMgDl = 0,
            lastPushedStale = true,
            unackedRepushCount = 0,
            consecutiveWearPushFailures = 0,
            activeServiceState = "running",
            activeServiceUpdatedAtEpochMs = 0L,
        )
}
