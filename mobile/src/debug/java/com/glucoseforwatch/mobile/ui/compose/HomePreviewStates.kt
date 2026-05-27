package com.glucoseforwatch.mobile.ui.compose

import android.content.Context
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.core.testing.SyncTestFixtures
import com.glucoseforwatch.mobile.settings.DexcomUserSettings
import com.glucoseforwatch.mobile.sync.PhoneSyncStateSnapshot
import com.glucoseforwatch.mobile.ui.HomeStateMapper
import com.glucoseforwatch.mobile.ui.HomeUiState
import com.glucoseforwatch.mobile.watch.WatchConnectionStatus
import com.glucoseforwatch.mobile.watch.WatchSyncHealthStatus

internal object HomePreviewStates {
    fun connected(context: Context): HomeUiState {
        val now = 1_700_000_000_000L
        return HomeStateMapper.map(
            context = context,
            dexcomSettings = DexcomUserSettings("alice", "secret", "OUS"),
            syncStatus =
                SyncTestFixtures.syncStatusSnapshot(
                    lastValueMgDl = 120,
                    lastTrend = "FLAT",
                    lastReadingTimestampEpochMs = now - 120_000L,
                    lastSyncEpochMs = now - 120_000L,
                ),
            watchStatus = connectedWatch(),
            watchHealth = installedWatchHealth(now),
            syncState = ackedSyncState(),
            batteryProtected = true,
            watchPushPending = false,
            activeSyncEnabled = true,
            displayUnit = GlucoseDisplayUnit.MG_DL,
            nowEpochMs = now,
        )
    }

    fun syncError(context: Context): HomeUiState =
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
            displayUnit = GlucoseDisplayUnit.MG_DL,
        )

    private fun connectedWatch(): WatchConnectionStatus =
        WatchConnectionStatus(
            connected = true,
            nodeId = "watch-1",
            displayName = "Pixel Watch 2",
            connectedWatches = emptyList(),
            preferredNodeId = "watch-1",
            preferredNodeMissing = false,
        )

    private fun installedWatchHealth(nowEpochMs: Long = 1_700_000_000_000L): WatchSyncHealthStatus =
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
            appVersionName = "0.6.0",
            appVersionCode = 25L,
            supportsTile = true,
            supportsComplication = true,
        )

    private fun ackedSyncState(): PhoneSyncStateSnapshot =
        emptySyncState().copy(lastPushSequenceId = 42L, lastAckSequenceId = 42L)

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
