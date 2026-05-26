package com.widgetg7.mobile

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.sync.ActiveGlucoseSyncController
import com.widgetg7.mobile.sync.PhoneAutoSyncScheduler
import com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine
import com.widgetg7.mobile.ui.DexcomEntryActivity
import com.widgetg7.mobile.ui.DexcomSettingsActivity
import com.widgetg7.mobile.ui.HomeUiBinder
import com.widgetg7.mobile.ui.HomeViewModel
import com.widgetg7.mobile.ui.ManualSyncFeedbackFormatter
import com.widgetg7.mobile.ui.NoticeActivity
import com.widgetg7.mobile.ui.WatchSetupActivity
import com.widgetg7.mobile.ui.WearInstallerActivity
import kotlinx.coroutines.launch

/** Home screen: glucose hero, companion status, settings rows, and manual sync. */
class MainActivity : AppCompatActivity() {
    private var baseScrollPaddingTop = 0
    private var baseScrollPaddingBottom = 0

    private val homeViewModel: HomeViewModel by viewModels()

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* denied is OK */ }

    private lateinit var syncNowButton: ImageButton
    private lateinit var homeWatchFaceValue: TextView
    private lateinit var homeWatchFaceMeta: TextView
    private lateinit var homeConnectionStatus: TextView
    private lateinit var homeBatteryStatus: TextView
    private lateinit var homeBatteryIcon: View
    private lateinit var homeStatusSeparator: View
    private lateinit var homeSyncAgeStatus: TextView
    private lateinit var homeSyncStatusLine: TextView
    private lateinit var homeBatterySettingSubtitle: TextView
    private lateinit var homeInstallSettingRow: View
    private lateinit var dexcomRowStatus: TextView
    private lateinit var watchRowStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainScrollView = findViewById<NestedScrollView>(R.id.mainScrollView)
        baseScrollPaddingTop = mainScrollView.paddingTop
        baseScrollPaddingBottom = mainScrollView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(mainScrollView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                baseScrollPaddingTop + systemBarsInsets.top,
                view.paddingRight,
                baseScrollPaddingBottom + systemBarsInsets.bottom,
            )
            insets
        }
        ViewCompat.requestApplyInsets(mainScrollView)

        syncNowButton = findViewById(R.id.syncNowButton)
        homeWatchFaceValue = findViewById(R.id.homeWatchFaceValue)
        homeWatchFaceMeta = findViewById(R.id.homeWatchFaceMeta)
        homeConnectionStatus = findViewById(R.id.homeConnectionStatus)
        homeBatteryStatus = findViewById(R.id.homeBatteryStatus)
        homeBatteryIcon = findViewById(R.id.homeBatteryIcon)
        homeStatusSeparator = findViewById(R.id.homeStatusSeparator)
        homeSyncAgeStatus = findViewById(R.id.homeSyncAgeStatus)
        homeSyncStatusLine = findViewById(R.id.homeSyncStatusLine)
        homeBatterySettingSubtitle = findViewById(R.id.homeBatterySettingSubtitle)
        homeInstallSettingRow = findViewById(R.id.homeInstallSettingRow)
        dexcomRowStatus = findViewById(R.id.dexcomRowStatus)
        watchRowStatus = findViewById(R.id.watchRowStatus)

        findViewById<View>(R.id.dexcomRow).setOnClickListener { openDexcomFlow() }
        findViewById<View>(R.id.watchRow).setOnClickListener { openWatchSetup() }
        findViewById<View>(R.id.homeBatterySettingRow).setOnClickListener { openBatterySettings() }
        findViewById<View>(R.id.homeInstallSettingRow).setOnClickListener { openWearInstaller() }
        findViewById<View>(R.id.openNoticeButton).setOnClickListener {
            startActivity(Intent(this, NoticeActivity::class.java))
        }
        findViewById<View>(R.id.homePermissionsRow).setOnClickListener { openAppDetailsSettings() }

        syncNowButton.setOnClickListener {
            lifecycleScope.launch { runManualSync() }
        }

        homeViewModel.uiState.observe(this) { state ->
            if (state == null) return@observe
            HomeUiBinder.bind(
                syncNowButton = syncNowButton,
                homeWatchFaceValue = homeWatchFaceValue,
                homeWatchFaceMeta = homeWatchFaceMeta,
                homeConnectionStatus = homeConnectionStatus,
                homeBatteryStatus = homeBatteryStatus,
                homeBatteryIcon = homeBatteryIcon,
                homeStatusSeparator = homeStatusSeparator,
                homeSyncAgeStatus = homeSyncAgeStatus,
                homeSyncStatusLine = homeSyncStatusLine,
                homeBatterySettingSubtitle = homeBatterySettingSubtitle,
                homeInstallSettingRow = homeInstallSettingRow,
                dexcomRowStatus = dexcomRowStatus,
                watchRowStatus = watchRowStatus,
                state = state,
            )
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
        syncNowButton.isEnabled = false
        syncNowButton.alpha = 0.45f

        try {
            val settingsStore = AppSettingsStore(this)
            if (!settingsStore.loadDexcomSettings().isConfigured()) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.manual_sync_dexcom_off),
                    Snackbar.LENGTH_LONG,
                ).show()
                return
            }

            ActiveGlucoseSyncController.syncNow(this)
            val result =
                PhoneGlucoseSyncEngine(this).run(
                    triggeredFromWatch = false,
                    forcePushCurrentReading = true,
                )
            Snackbar.make(
                findViewById(android.R.id.content),
                ManualSyncFeedbackFormatter.format(this, result),
                Snackbar.LENGTH_LONG,
            ).show()
        } finally {
            refreshHome()
            syncNowButton.isEnabled = true
            syncNowButton.alpha = 1f
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

    private fun openWearInstaller() {
        startActivity(Intent(this, WearInstallerActivity::class.java))
    }

    private fun openBatterySettings() {
        runCatching {
            startActivity(com.widgetg7.mobile.battery.BatteryOptimizationHelper(this).buildSettingsIntent())
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
