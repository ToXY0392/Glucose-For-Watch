package com.widgetg7.mobile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View as AndroidView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.LaunchStateStore
import com.widgetg7.mobile.status.SyncErrorCategory
import com.widgetg7.mobile.status.SyncStatusRepository
import com.widgetg7.mobile.status.SyncStatusSnapshot
import com.widgetg7.mobile.sync.PhoneAutoSyncScheduler
import com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine
import com.widgetg7.mobile.sync.SyncExecutionResult
import com.widgetg7.mobile.ui.DexcomEntryActivity
import com.widgetg7.mobile.ui.NoticeActivity
import com.widgetg7.mobile.ui.WatchSetupActivity
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchConnectionStatus
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val logTag = "WidgetG7Phone"
    private var baseScrollPaddingTop = 0

    private lateinit var watchSettingsButton: TextView
    private lateinit var watchModelText: TextView
    private lateinit var watchFaceStatusText: TextView
    private lateinit var watchRefreshButton: TextView

    private lateinit var dexcomCardHeader: LinearLayout
    private lateinit var dexcomStatusDot: AndroidView
    private lateinit var dexcomCardSummaryText: TextView
    private lateinit var dexcomCardContent: LinearLayout
    private lateinit var configureDexcomButton: Button

    private lateinit var permissionsCardHeader: LinearLayout
    private lateinit var permissionsCardContent: LinearLayout
    private lateinit var notificationsStepsText: TextView
    private lateinit var batteryStepsText: TextView
    private lateinit var openNoticeButton: TextView
    private lateinit var openNotificationsSettingsButton: Button
    private lateinit var openAppSettingsButton: Button

    private var dexcomConnected = false
    private var dexcomExpanded = false
    private var permissionsExpanded = false
    private var dexcomSectionTouched = false
    private var permissionsSectionTouched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PhoneAutoSyncScheduler.schedule(this)

        val mainScrollView = findViewById<ScrollView>(R.id.mainScrollView)
        baseScrollPaddingTop = mainScrollView.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(mainScrollView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                baseScrollPaddingTop + systemBarsInsets.top,
                view.paddingRight,
                view.paddingBottom,
            )
            insets
        }
        ViewCompat.requestApplyInsets(mainScrollView)

        watchSettingsButton = findViewById(R.id.watchSettingsButton)
        watchModelText = findViewById(R.id.watchModelText)
        watchFaceStatusText = findViewById(R.id.watchFaceStatusText)
        watchRefreshButton = findViewById(R.id.watchRefreshButton)

        dexcomCardHeader = findViewById(R.id.dexcomCardHeader)
        dexcomStatusDot = findViewById(R.id.dexcomStatusDot)
        dexcomCardSummaryText = findViewById(R.id.dexcomCardSummaryText)
        dexcomCardContent = findViewById(R.id.dexcomCardContent)
        configureDexcomButton = findViewById(R.id.configureDexcomButton)

        permissionsCardHeader = findViewById(R.id.permissionsCardHeader)
        permissionsCardContent = findViewById(R.id.permissionsCardContent)
        notificationsStepsText = findViewById(R.id.notificationsStepsText)
        batteryStepsText = findViewById(R.id.batteryStepsText)
        openNoticeButton = findViewById(R.id.openNoticeButton)
        openNotificationsSettingsButton = findViewById(R.id.openNotificationsSettingsButton)
        openAppSettingsButton = findViewById(R.id.openAppSettingsButton)

        watchSettingsButton.setOnClickListener { showWatchSettingsMenu() }
        watchRefreshButton.setOnClickListener {
            lifecycleScope.launch { runManualSync() }
        }

        dexcomCardHeader.setOnClickListener {
            dexcomSectionTouched = true
            dexcomExpanded = !dexcomExpanded
            if (dexcomExpanded) {
                permissionsExpanded = false
            }
            renderCardStates()
        }
        configureDexcomButton.setOnClickListener { handleDexcomAction() }

        permissionsCardHeader.setOnClickListener {
            permissionsSectionTouched = true
            permissionsExpanded = !permissionsExpanded
            if (permissionsExpanded) {
                dexcomExpanded = false
            }
            renderCardStates()
        }
        openNotificationsSettingsButton.setOnClickListener { openAppNotificationSettings() }
        openAppSettingsButton.setOnClickListener { openAppDetailsSettings() }
        openNoticeButton.setOnClickListener { startActivity(Intent(this, NoticeActivity::class.java)) }

        refreshHome()
    }

    override fun onResume() {
        super.onResume()
        refreshHome()
    }

    private fun refreshHome() {
        val syncStatus = SyncStatusRepository(this).load()
        val dexcomSettings = AppSettingsStore(this).loadDexcomSettings()
        dexcomConnected = dexcomSettings.isConfigured()
        dexcomCardSummaryText.text = dexcomSummary(dexcomSettings, syncStatus)
        applyDexcomStatusStyle(dexcomConnected)
        configureDexcomButton.text = if (dexcomConnected) "Déconnexion" else "Se connecter"

        notificationsStepsText.text =
            "Paramètres > Applications > Widget G7 Phone > Notifications > Autoriser."
        batteryStepsText.text =
            "Paramètres > Applications > Widget G7 Phone > Batterie > Autoriser l'utilisation en arrière-plan."

        if (!dexcomSectionTouched) {
            dexcomExpanded = shouldOpenDexcomSection(dexcomSettings.isConfigured(), syncStatus.lastErrorCategory)
        }
        if (!permissionsSectionTouched) {
            permissionsExpanded = false
        }

        lifecycleScope.launch {
            val watchStatus = WatchConnectionRepository(this@MainActivity).loadStatus()
            watchModelText.text = watchModelLabel(watchStatus)
            watchFaceStatusText.text = watchSummary(watchStatus)
            applyWatchStatusStyle(watchStatus)
        }

        renderCardStates()
    }

    private fun handleDexcomAction() {
        if (!dexcomConnected) {
            startActivity(Intent(this, DexcomEntryActivity::class.java))
            return
        }

        AppSettingsStore(this).clearDexcomSettings()
        LaunchStateStore(this).resetDexcomEntry()
        SyncStatusRepository(this).clearSessionState()
        dexcomConnected = false
        refreshHome()
    }

    private suspend fun runManualSync() {
        watchRefreshButton.isEnabled = false
        watchRefreshButton.text = "…"

        when (val result = PhoneGlucoseSyncEngine(this).run(triggeredFromWatch = false)) {
            is SyncExecutionResult.Success ->
                Log.d(logTag, "Manual sync completed source=${result.sourceName}")

            is SyncExecutionResult.Failure ->
                Log.d(logTag, "Manual sync failed message=${result.message}")
        }

        refreshHome()
        watchRefreshButton.isEnabled = true
        watchRefreshButton.text = "↻"
    }

    private fun showWatchSettingsMenu() {
        val menu = PopupMenu(this, watchSettingsButton)
        menu.menu.add(0, 1, 0, "Configurer la montre")
        menu.menu.add(0, 2, 1, "Ouvrir le Bluetooth")
        menu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    startActivity(Intent(this, WatchSetupActivity::class.java))
                    true
                }

                2 -> {
                    openSystemSettings(
                        primaryIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS),
                        fallbackIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS),
                    )
                    true
                }

                else -> false
            }
        }
        menu.show()
    }

    private fun openSystemSettings(primaryIntent: Intent, fallbackIntent: Intent) {
        try {
            if (primaryIntent.resolveActivity(packageManager) != null) {
                startActivity(primaryIntent)
            } else {
                startActivity(fallbackIntent)
            }
        } catch (_: ActivityNotFoundException) {
            try {
                startActivity(fallbackIntent)
            } catch (fallbackError: ActivityNotFoundException) {
                Log.w(logTag, "Unable to open system settings", fallbackError)
            }
        }
    }

    private fun openAppDetailsSettings() {
        val appDetailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }

        try {
            startActivity(appDetailsIntent)
        } catch (_: ActivityNotFoundException) {
            Log.w(logTag, "Unable to open app details settings")
        }
    }

    private fun openAppNotificationSettings() {
        val appNotificationIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }

        try {
            if (appNotificationIntent.resolveActivity(packageManager) != null) {
                startActivity(appNotificationIntent)
            } else {
                openAppDetailsSettings()
            }
        } catch (_: ActivityNotFoundException) {
            openAppDetailsSettings()
        }
    }

    private fun dexcomSummary(
        settings: com.widgetg7.mobile.settings.DexcomUserSettings,
        syncStatus: SyncStatusSnapshot,
    ): String {
        return when {
            !settings.isConfigured() -> "Non connecté"
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 -> "Non connecté"
            else -> "Connecté"
        }
    }

    private fun watchSummary(watchStatus: WatchConnectionStatus): String =
        if (watchStatus.connected) "Connectée" else "Non connectée"

    private fun watchModelLabel(watchStatus: WatchConnectionStatus): String =
        if (watchStatus.connected && watchStatus.displayName.isNotBlank()) watchStatus.displayName else "Aucune montre"

    private fun applyWatchStatusStyle(watchStatus: WatchConnectionStatus) {
        if (watchStatus.connected) {
            watchFaceStatusText.setTextColor(ContextCompat.getColor(this, R.color.wg7_watch_green))
        } else {
            watchFaceStatusText.setTextColor(ContextCompat.getColor(this, R.color.wg7_danger))
        }
    }

    private fun applyDexcomStatusStyle(connected: Boolean) {
        if (connected) {
            dexcomStatusDot.setBackgroundResource(R.drawable.bg_watch_status_dot_connected)
            dexcomCardSummaryText.setTextColor(ContextCompat.getColor(this, R.color.wg7_success))
        } else {
            dexcomStatusDot.setBackgroundResource(R.drawable.bg_watch_status_dot_disconnected)
            dexcomCardSummaryText.setTextColor(ContextCompat.getColor(this, R.color.wg7_danger))
        }
    }

    private fun shouldOpenDexcomSection(
        dexcomConfigured: Boolean,
        lastErrorCategory: SyncErrorCategory?,
    ): Boolean {
        return !dexcomConfigured || lastErrorCategory == SyncErrorCategory.AUTH
    }

    private fun renderCardStates() {
        dexcomCardContent.visibility = if (dexcomExpanded) View.VISIBLE else View.GONE
        permissionsCardContent.visibility = if (permissionsExpanded) View.VISIBLE else View.GONE
    }
}
