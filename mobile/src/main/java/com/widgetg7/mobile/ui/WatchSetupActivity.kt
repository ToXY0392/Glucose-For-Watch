package com.widgetg7.mobile.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.widgetg7.core.model.GlucoseUnitFormatter
import com.widgetg7.mobile.R
import com.widgetg7.mobile.battery.BatteryOptimizationHelper
import com.widgetg7.mobile.settings.DisplaySettingsStore
import com.widgetg7.mobile.ui.compose.WatchSetupScreen
import com.widgetg7.mobile.ui.compose.WatchSetupUiState
import com.widgetg7.mobile.ui.theme.WidgetG7Theme
import com.widgetg7.mobile.watch.ConnectedWatchNode
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchSyncVerifier
import kotlinx.coroutines.launch

/** Watch selection, battery optimization, and install/test flows. */
class WatchSetupActivity : ComponentActivity() {
    private var uiState by mutableStateOf(WatchSetupUiState())
    private var connectedWatches: List<ConnectedWatchNode> = emptyList()
    private var applyingSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            WidgetG7Theme {
                WatchSetupScreen(
                    state = uiState,
                    onWatchSelected = { index ->
                        if (applyingSelection) return@WatchSetupScreen
                        val selectedWatch = connectedWatches.getOrNull(index) ?: return@WatchSetupScreen
                        WatchConnectionRepository(this).savePreferredWatch(selectedWatch)
                        uiState = uiState.copy(selectedWatchIndex = index)
                    },
                    onBatteryOptimization = {
                        runCatching {
                            startActivity(BatteryOptimizationHelper(this).buildSettingsIntent())
                        }
                    },
                    onInstallWear = {
                        startActivity(Intent(this, WearInstallerActivity::class.java))
                    },
                    onWatchTest = {
                        lifecycleScope.launch { runWatchTest() }
                    },
                    onBack = { finish() },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                )
            }
        }

        refreshOptions()
    }

    override fun onResume() {
        super.onResume()
        refreshOptions()
    }

    private fun refreshOptions() {
        lifecycleScope.launch {
            refreshWatchChoices()
            refreshBatteryOptimizationStatus()
            refreshWatchTestAvailability()
        }
    }

    private suspend fun refreshWatchTestAvailability() {
        val watchStatus = WatchConnectionRepository(this).loadStatus()
        uiState =
            uiState.copy(
                watchTestEnabled = watchStatus.connected,
                watchTestLabel = getString(R.string.home_watch_test),
            )
    }

    private suspend fun runWatchTest() {
        uiState =
            uiState.copy(
                watchTestEnabled = false,
                watchTestLabel = getString(R.string.home_watch_test_running),
            )

        val result = WatchSyncVerifier(this).runTest()
        val message =
            when (result) {
                WatchSyncVerifier.Result.NoWatch -> getString(R.string.home_watch_test_no_watch)
                WatchSyncVerifier.Result.NoDexcom -> getString(R.string.home_watch_test_no_dexcom)
                WatchSyncVerifier.Result.Timeout -> getString(R.string.home_watch_test_timeout)
                WatchSyncVerifier.Result.SendFailed -> getString(R.string.home_watch_test_failed)
                is WatchSyncVerifier.Result.Error ->
                    result.message?.takeIf { it.isNotBlank() }
                        ?: getString(R.string.home_watch_test_failed)
                is WatchSyncVerifier.Result.Sent -> {
                    val unit = DisplaySettingsStore(this).loadGlucoseDisplayUnit()
                    val formatted = GlucoseUnitFormatter.formatWithUnit(result.valueMgDl, unit)
                    getString(R.string.home_watch_test_sent, formatted)
                }
            }

        Snackbar.make(window.decorView, message, Snackbar.LENGTH_SHORT).show()
        refreshWatchTestAvailability()
    }

    private fun refreshBatteryOptimizationStatus() {
        val status = BatteryOptimizationHelper(this).loadStatus()
        uiState =
            uiState.copy(
                batteryButtonLabel =
                    if (status.isProtectedFromOptimization) {
                        getString(R.string.watch_setup_battery_ok)
                    } else {
                        getString(R.string.watch_setup_battery_prompt)
                    },
                batteryButtonEnabled = !status.isProtectedFromOptimization,
            )
    }

    private suspend fun refreshWatchChoices() {
        val repository = WatchConnectionRepository(this)
        connectedWatches = repository.loadConnectedWatches()

        val showSelector = connectedWatches.size > 1
        if (!showSelector) {
            uiState =
                uiState.copy(
                    showWatchSelector = false,
                    watchNames = emptyList(),
                )
            return
        }

        val preferredNodeId = repository.loadPreferredWatchId()
        val selectedIndex =
            connectedWatches.indexOfFirst { it.nodeId == preferredNodeId }.takeIf { it >= 0 } ?: 0

        applyingSelection = true
        uiState =
            uiState.copy(
                showWatchSelector = true,
                watchNames = connectedWatches.map { it.displayName },
                selectedWatchIndex = selectedIndex,
            )
        applyingSelection = false
    }
}
