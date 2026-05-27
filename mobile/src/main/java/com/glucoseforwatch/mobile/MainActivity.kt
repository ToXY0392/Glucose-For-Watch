package com.glucoseforwatch.mobile

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.glucoseforwatch.mobile.settings.AppSettingsStore
import com.glucoseforwatch.mobile.sync.ActiveGlucoseSyncController
import com.glucoseforwatch.mobile.sync.PhoneAutoSyncScheduler
import com.glucoseforwatch.mobile.sync.PhoneGlucoseSyncEngine
import com.glucoseforwatch.mobile.ui.DexcomEntryActivity
import com.glucoseforwatch.mobile.ui.DexcomSettingsActivity
import com.glucoseforwatch.mobile.ui.HomeViewModel
import com.glucoseforwatch.mobile.ui.ManualSyncFeedbackFormatter
import com.glucoseforwatch.mobile.ui.NoticeActivity
import com.glucoseforwatch.mobile.ui.GlucoseUnitSettingsActivity
import com.glucoseforwatch.mobile.ui.WatchSetupActivity
import com.glucoseforwatch.mobile.ui.WearInstallerActivity
import com.glucoseforwatch.mobile.ui.compose.HomeScreen
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme
import kotlinx.coroutines.launch

/** Home screen: glucose hero, companion status, settings rows, and manual sync. */
class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val snackbarHostState = SnackbarHostState()
    private var syncBusy by mutableStateOf(false)

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* denied is OK */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlucoseForWatchTheme {
                val uiState by homeViewModel.uiState.observeAsState()
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = Color.Transparent,
                ) { padding ->
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding)
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
                            state = uiState,
                            syncEnabled = !syncBusy,
                            onSyncClick = { lifecycleScope.launch { runManualSync() } },
                            onDexcomClick = { openDexcomFlow() },
                            onWatchClick = { openWatchSetup() },
                            onUnitClick = { openGlucoseUnitSettings() },
                            onBatteryClick = { openBatterySettings() },
                            onInstallClick = { openWearInstaller() },
                            onNoticeClick = {
                                startActivity(Intent(this@MainActivity, NoticeActivity::class.java))
                            },
                            onPermissionsClick = { openAppDetailsSettings() },
                        )
                    }
                }
            }
        }

        scheduleAutoSyncIfReady()
        requestNotificationPermissionIfNeeded()
        refreshHome()
    }

    override fun onResume() {
        super.onResume()
        scheduleAutoSyncIfReady()
        refreshHome()
    }

    private fun refreshHome() {
        homeViewModel.refresh(this)
    }

    private suspend fun runManualSync() {
        syncBusy = true

        try {
            val settingsStore = AppSettingsStore(this)
            if (!settingsStore.loadDexcomSettings().isConfigured()) {
                snackbarHostState.showSnackbar(getString(R.string.manual_sync_dexcom_off))
                return
            }

            ActiveGlucoseSyncController.syncNow(this)
            val result =
                PhoneGlucoseSyncEngine(this).run(
                    triggeredFromWatch = false,
                    forcePushCurrentReading = true,
                )
            snackbarHostState.showSnackbar(ManualSyncFeedbackFormatter.format(this, result))
        } finally {
            refreshHome()
            syncBusy = false
        }
    }

    private fun openDexcomFlow() {
        val settings = AppSettingsStore(this).loadDexcomSettings()
        if (settings.isConfigured()) {
            startActivity(Intent(this, DexcomSettingsActivity::class.java))
        } else {
            startActivity(Intent(this, DexcomEntryActivity::class.java))
        }
    }

    private fun openWatchSetup() {
        startActivity(Intent(this, WatchSetupActivity::class.java))
    }

    private fun openGlucoseUnitSettings() {
        startActivity(Intent(this, GlucoseUnitSettingsActivity::class.java))
    }

    private fun openWearInstaller() {
        startActivity(Intent(this, WearInstallerActivity::class.java))
    }

    private fun openBatterySettings() {
        runCatching {
            startActivity(com.glucoseforwatch.mobile.battery.BatteryOptimizationHelper(this).buildSettingsIntent())
        }
    }

    private fun scheduleAutoSyncIfReady() {
        val settingsStore = AppSettingsStore(this)
        if (!settingsStore.loadDexcomSettings().isConfigured()) {
            return
        }
        if (settingsStore.isActiveSyncEnabled()) {
            ActiveGlucoseSyncController.start(this)
        } else {
            PhoneAutoSyncScheduler.schedule(this, delayMs = 5_000L)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun openAppDetailsSettings() {
        val appDetailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }

        try {
            startActivity(appDetailsIntent)
        } catch (_: ActivityNotFoundException) {
            // No settings activity available on this device.
        }
    }
}
