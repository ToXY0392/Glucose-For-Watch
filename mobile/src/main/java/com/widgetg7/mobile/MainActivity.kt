package com.widgetg7.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.battery.BatteryOptimizationHelper
import com.widgetg7.mobile.data.PhoneGlucoseSourceFactory
import com.widgetg7.mobile.dexcom.DexcomShareErrorKind
import com.widgetg7.mobile.dexcom.DexcomShareException
import com.widgetg7.mobile.notifications.NotificationHelper
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.status.SyncErrorCategory
import com.widgetg7.mobile.status.SyncStatusRepository
import com.widgetg7.mobile.sync.PhoneAutoSyncScheduler
import com.widgetg7.mobile.sync.PhoneWearSyncService
import com.widgetg7.mobile.ui.DexcomSettingsActivity
import com.widgetg7.mobile.ui.WatchSetupActivity
import com.widgetg7.mobile.watch.WatchConnectionRepository
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    private val logTag = "WidgetG7Phone"

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        Log.d(logTag, "Notification permission granted=$granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PhoneAutoSyncScheduler.schedule(this)

        findViewById<Button>(R.id.refreshNowButton).setOnClickListener {
            lifecycleScope.launch { runManualSync() }
        }

        findViewById<Button>(R.id.improveSyncButton).setOnClickListener {
            startActivity(BatteryOptimizationHelper(this).buildSettingsIntent())
        }

        findViewById<Button>(R.id.configureDexcomButton).setOnClickListener {
            startActivity(Intent(this, DexcomSettingsActivity::class.java))
        }

        findViewById<Button>(R.id.configureWatchButton).setOnClickListener {
            startActivity(Intent(this, WatchSetupActivity::class.java))
        }

        requestNotificationPermissionIfNeeded()
        refreshHome()
    }

    override fun onResume() {
        super.onResume()
        refreshHome()
    }

    private fun refreshHome() {
        val syncStatus = SyncStatusRepository(this).load()
        val dexcomSettings = AppSettingsStore(this).loadDexcomSettings()
        val batteryStatus = BatteryOptimizationHelper(this).loadStatus()

        val glucoseValueText = findViewById<TextView>(R.id.glucoseValueText)
        val glucoseTrendText = findViewById<TextView>(R.id.glucoseTrendText)
        val lastSyncText = findViewById<TextView>(R.id.lastSyncText)
        val dexcomStatusText = findViewById<TextView>(R.id.dexcomStatusText)
        val watchStatusText = findViewById<TextView>(R.id.watchStatusText)
        val batteryStatusText = findViewById<TextView>(R.id.batteryStatusText)
        val syncStatusText = findViewById<TextView>(R.id.syncStatusText)

        glucoseValueText.text = syncStatus.lastValueMgDl?.toString() ?: "--"
        glucoseTrendText.text = when {
            syncStatus.lastTrend.isBlank() -> "Derniere tendance indisponible"
            else -> "Tendance: ${displayTrend(syncStatus.lastTrend)}"
        }
        lastSyncText.text = if (syncStatus.lastSyncEpochMs > 0L) {
            "Derniere sync: ${DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(syncStatus.lastSyncEpochMs))}"
        } else {
            "Derniere sync: aucune pour le moment"
        }
        dexcomStatusText.text = if (dexcomSettings.isConfigured()) {
            when {
                syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                    "Dexcom: reconnexion requise (${displayServer(dexcomSettings.server)})"

                syncStatus.hasSuccessfulSync() -> "Dexcom: connecte (${displayServer(dexcomSettings.server)})"
                else -> "Dexcom: configure (${displayServer(dexcomSettings.server)})"
            }
        } else {
            "Dexcom: a configurer"
        }
        syncStatusText.text = when {
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                "Etat: reconnectez votre compte Dexcom"

            syncStatus.lastError.isNotBlank() -> "Etat: ${syncStatus.lastError}"
            syncStatus.hasSuccessfulSync() -> "Etat: synchronisation active (${syncStatus.lastSourceName})"
            else -> "Etat: en attente d'une premiere synchronisation"
        }
        batteryStatusText.text = batteryStatus.label()

        lifecycleScope.launch {
            val watchStatus = WatchConnectionRepository(this@MainActivity).loadStatus()
            watchStatusText.text = watchStatus.label()
        }
    }

    private suspend fun runManualSync() {
        val syncStatusText = findViewById<TextView>(R.id.syncStatusText)
        val source = PhoneGlucoseSourceFactory.create(this)
        val syncStatusRepository = SyncStatusRepository(this)
        val notificationHelper = NotificationHelper(this)

        try {
            Log.d(logTag, "Manual sync started with source=${source.sourceName}")
            syncStatusText.text = "Etat: actualisation en cours..."
            val reading = source.latest()
            PhoneWearSyncService(this).pushLatest(reading)
            syncStatusRepository.saveSuccess(source.sourceName, reading)
            notificationHelper.cancelSyncAlerts()
            Log.d(logTag, "Manual sync push completed")
        } catch (t: Throwable) {
            Log.e(logTag, "Manual sync failed", t)
            val category = toCategory(t)
            syncStatusRepository.saveError(
                message = toUserMessage(t),
                category = category,
            )
            notifyIfNeeded(syncStatusRepository.load(), notificationHelper)
        }

        refreshHome()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun notifyIfNeeded(syncStatus: com.widgetg7.mobile.status.SyncStatusSnapshot, notificationHelper: NotificationHelper) {
        when {
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                notificationHelper.notifyDexcomReconnectRequired()

            syncStatus.consecutiveFailureCount >= 3 ->
                notificationHelper.notifySyncInterrupted(syncStatus.lastError.ifBlank { "La synchronisation a besoin de votre attention." })
        }
    }

    private fun displayServer(server: String): String = if (server.equals("US", true)) "US" else "Europe"

    private fun displayTrend(trend: String): String = when (trend) {
        "UP" -> "en hausse"
        "UP_RIGHT" -> "en hausse legere"
        "FLAT" -> "stable"
        "DOWN_RIGHT" -> "en baisse legere"
        "DOWN" -> "en baisse"
        else -> trend
    }

    private fun toCategory(t: Throwable): SyncErrorCategory {
        if (t is DexcomShareException) {
            return when (t.kind) {
                DexcomShareErrorKind.AUTH -> SyncErrorCategory.AUTH
                DexcomShareErrorKind.NETWORK -> SyncErrorCategory.NETWORK
                DexcomShareErrorKind.NO_DATA -> SyncErrorCategory.OTHER
                DexcomShareErrorKind.UNKNOWN -> SyncErrorCategory.OTHER
            }
        }
        return SyncErrorCategory.OTHER
    }

    private fun toUserMessage(t: Throwable): String {
        return when {
            t is DexcomShareException -> t.message
            else -> t.message ?: "Erreur inconnue"
        }
    }
}
