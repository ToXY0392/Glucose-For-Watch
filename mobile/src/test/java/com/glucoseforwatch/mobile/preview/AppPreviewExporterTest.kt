package com.glucoseforwatch.mobile.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.test.core.app.ApplicationProvider
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.core.testing.SyncTestFixtures
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.settings.DexcomUserSettings
import com.glucoseforwatch.mobile.sync.PhoneSyncStateSnapshot
import com.glucoseforwatch.mobile.ui.HomeStateMapper
import com.glucoseforwatch.mobile.ui.HomeUiState
import com.glucoseforwatch.mobile.ui.compose.HomeScreen
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme
import com.glucoseforwatch.mobile.watch.WatchConnectionStatus
import com.glucoseforwatch.mobile.watch.WatchSyncHealthStatus
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

/**
 * Renders home preview states to PNG for design review (M.3).
 * Uses [HomeScreen] Compose (same surface as runtime [MainActivity]).
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppPreviewExporterTest {
    @Test
    fun exportMobileHomePreview_connected() {
        exportPreview("mobile-home-connected", connectedState())
    }

    @Test
    fun exportMobileHomePreview_connected_mmol() {
        exportPreview("mobile-home-connected-mmol", connectedState(GlucoseDisplayUnit.MMOL_L))
    }

    @Test
    fun exportMobileHomePreview_dexcomOff() {
        exportPreview("mobile-home-dexcom-off", dexcomOffState())
    }

    @Test
    fun exportMobileHomePreview_waiting() {
        exportPreview("mobile-home-waiting", waitingState())
    }

    @Test
    fun exportMobileHomePreview_pushPending() {
        exportPreview("mobile-home-push-pending", pushPendingState())
    }

    @Test
    fun exportMobileHomePreview_syncError() {
        exportPreview("mobile-home-sync-error", syncErrorState())
    }

    @Test
    fun exportMobileHomePreview_watchOffline() {
        exportPreview("mobile-home-watch-offline", watchOfflineState())
    }

    private fun exportPreview(fileStem: String, state: HomeUiState) {
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val composeView = ComposeView(activity)
        composeView.setContent {
            GlucoseForWatchTheme {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            colorResource(R.color.gfw_canvas_start),
                                            colorResource(R.color.gfw_canvas_end),
                                        ),
                                ),
                            ),
                ) {
                    HomeScreen(
                        state = state,
                        syncEnabled = true,
                        onSyncClick = {},
                        onDexcomClick = {},
                        onWatchClick = {},
                        onUnitClick = {},
                        onBatteryClick = {},
                        onPermissionsClick = {},
                        onAboutClick = {},
                    )
                }
            }
        }
        activity.setContentView(composeView)

        val bitmap = captureView(composeView)
        val outDir = File("build/app-previews").also { it.mkdirs() }
        val outFile = File(outDir, "$fileStem.png")
        FileOutputStream(outFile).use { stream ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) { "PNG encode failed" }
        }
        println("APP_PREVIEW=${outFile.absolutePath}")
    }

    private fun connectedState(displayUnit: GlucoseDisplayUnit = GlucoseDisplayUnit.MG_DL): HomeUiState {
        val context = ApplicationProvider.getApplicationContext<Context>()
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
            displayUnit = displayUnit,
            nowEpochMs = now,
        )
    }

    private fun dexcomOffState(): HomeUiState {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return HomeStateMapper.map(
            context = context,
            dexcomSettings = DexcomUserSettings("", "", "OUS"),
            syncStatus = SyncTestFixtures.syncStatusSnapshot(lastValueMgDl = null),
            watchStatus = connectedWatch(),
            watchHealth = installedWatchHealth(),
            syncState = emptySyncState(),
            batteryProtected = false,
            watchPushPending = false,
            activeSyncEnabled = true,
            displayUnit = GlucoseDisplayUnit.MG_DL,
        )
    }

    private fun waitingState(): HomeUiState {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return HomeStateMapper.map(
            context = context,
            dexcomSettings = DexcomUserSettings("alice", "secret", "OUS"),
            syncStatus = SyncTestFixtures.syncStatusSnapshot(lastValueMgDl = null),
            watchStatus = connectedWatch(),
            watchHealth = installedWatchHealth(),
            syncState = emptySyncState(),
            batteryProtected = true,
            watchPushPending = false,
            activeSyncEnabled = true,
            displayUnit = GlucoseDisplayUnit.MG_DL,
        )
    }

    private fun pushPendingState(): HomeUiState {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val now = 1_700_000_000_000L
        return HomeStateMapper.map(
            context = context,
            dexcomSettings = DexcomUserSettings("alice", "secret", "OUS"),
            syncStatus =
                SyncTestFixtures.syncStatusSnapshot(
                    lastValueMgDl = 120,
                    lastReadingTimestampEpochMs = now - 60_000L,
                    watchPushPending = true,
                ),
            watchStatus = connectedWatch(),
            watchHealth = installedWatchHealth(now),
            syncState = emptySyncState().copy(lastPushSequenceId = 10L),
            batteryProtected = true,
            watchPushPending = true,
            activeSyncEnabled = true,
            displayUnit = GlucoseDisplayUnit.MG_DL,
            nowEpochMs = now,
        )
    }

    private fun syncErrorState(): HomeUiState {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return HomeStateMapper.map(
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
    }

    private fun watchOfflineState(): HomeUiState {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return HomeStateMapper.map(
            context = context,
            dexcomSettings = DexcomUserSettings("alice", "secret", "OUS"),
            syncStatus = SyncTestFixtures.syncStatusSnapshot(lastValueMgDl = 120),
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
            batteryProtected = true,
            watchPushPending = false,
            activeSyncEnabled = true,
            displayUnit = GlucoseDisplayUnit.MG_DL,
        )
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
            appVersionName = "0.4.0",
            appVersionCode = 23L,
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

    private fun captureView(view: View): Bitmap {
        val widthPx = 1080
        val heightPx = 2400
        view.measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.AT_MOST),
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap =
            Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888,
            )
        view.draw(Canvas(bitmap))
        return bitmap
    }
}
