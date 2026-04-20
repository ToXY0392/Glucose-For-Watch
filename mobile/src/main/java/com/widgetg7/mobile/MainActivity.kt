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
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.status.SyncStatusRepository
import com.widgetg7.mobile.sync.PhoneAutoSyncScheduler
import com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine
import com.widgetg7.mobile.sync.SyncExecutionResult
import com.widgetg7.mobile.sync.SyncText
import com.widgetg7.mobile.ui.DexcomSettingsActivity
import com.widgetg7.mobile.ui.WatchSetupActivity
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchConnectionStatus
import com.widgetg7.mobile.watch.WatchSyncHealthRepository
import com.widgetg7.mobile.watch.WatchSyncHealthStatus
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val logTag = "WidgetG7Phone"
    private lateinit var refreshNowButton: Button
    private lateinit var improveSyncButton: Button
    private lateinit var appStatusHeadlineText: TextView
    private lateinit var appStatusDetailText: TextView
    private lateinit var setupHintText: TextView
    private lateinit var lastSyncText: TextView
    private lateinit var dexcomStatusText: TextView
    private lateinit var watchStatusText: TextView
    private lateinit var batteryStatusText: TextView
    private lateinit var syncStatusText: TextView

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        Log.d(logTag, "Notification permission granted=$granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PhoneAutoSyncScheduler.schedule(this)

        refreshNowButton = findViewById(R.id.refreshNowButton)
        improveSyncButton = findViewById(R.id.improveSyncButton)
        appStatusHeadlineText = findViewById(R.id.appStatusHeadlineText)
        appStatusDetailText = findViewById(R.id.appStatusDetailText)
        setupHintText = findViewById(R.id.setupHintText)
        lastSyncText = findViewById(R.id.lastSyncText)
        dexcomStatusText = findViewById(R.id.dexcomStatusText)
        watchStatusText = findViewById(R.id.watchStatusText)
        batteryStatusText = findViewById(R.id.batteryStatusText)
        syncStatusText = findViewById(R.id.syncStatusText)

        refreshNowButton.setOnClickListener {
            lifecycleScope.launch { runManualSync() }
        }

        improveSyncButton.setOnClickListener {
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
        val watchHealth = WatchSyncHealthRepository(this).load()

        lastSyncText.text = SyncText.lastSync(syncStatus)
        dexcomStatusText.text = SyncText.dexcomStatus(dexcomSettings, syncStatus)
        syncStatusText.text = SyncText.syncStatus(syncStatus)
        batteryStatusText.text = batteryStatus.label()
        improveSyncButton.visibility = if (batteryStatus.isProtectedFromOptimization) android.view.View.GONE else android.view.View.VISIBLE

        lifecycleScope.launch {
            val watchStatus = WatchConnectionRepository(this@MainActivity).loadStatus()
            watchStatusText.text = buildWatchStatusLabel(watchStatus, watchHealth)
            val overview = buildSetupOverview(
                dexcomConfigured = dexcomSettings.isConfigured(),
                watchStatus = watchStatus,
                hasSuccessfulSync = syncStatus.hasSuccessfulSync(),
                batteryProtected = batteryStatus.isProtectedFromOptimization,
                watchHealth = watchHealth,
            )
            appStatusHeadlineText.text = overview.headline
            appStatusDetailText.text = overview.detail
            setupHintText.text = overview.hint
        }
    }

    private suspend fun runManualSync() {
        refreshNowButton.isEnabled = false
        refreshNowButton.text = "Actualisation..."
        syncStatusText.text = "État : actualisation en cours..."

        when (val result = PhoneGlucoseSyncEngine(this).run(triggeredFromWatch = false)) {
            is SyncExecutionResult.Success ->
                Log.d(logTag, "Manual sync completed source=${result.sourceName}")

            is SyncExecutionResult.Failure -> {
                syncStatusText.text = "État : ${result.message}"
                Log.d(logTag, "Manual sync failed message=${result.message}")
            }
        }

        refreshHome()
        refreshNowButton.isEnabled = true
        refreshNowButton.text = "Vérifier la synchronisation"
    }

    private fun buildSetupOverview(
        dexcomConfigured: Boolean,
        watchStatus: WatchConnectionStatus,
        hasSuccessfulSync: Boolean,
        batteryProtected: Boolean,
        watchHealth: WatchSyncHealthStatus?,
    ): SetupOverview {
        return when {
            !dexcomConfigured -> SetupOverview(
                headline = "Dexcom à configurer",
                detail = "Le téléphone ne peut pas alimenter le tile ni le widget tant que le compte Dexcom Share n'est pas configuré.",
                hint = "Commencez par Configurer Dexcom, puis revenez ici pour vérifier la montre.",
            )

            !watchStatus.connected -> SetupOverview(
                headline = "Montre à relier",
                detail = "Le compte Dexcom est prêt, mais aucune montre connectée n'a été détectée pour recevoir les mises à jour.",
                hint = "Ouvrez Configurer la montre puis ajoutez ensuite le tile ou la complication.",
            )

            watchHealth?.syncLimited == true -> SetupOverview(
                headline = "Sync montre limitée",
                detail = "La montre signale une sync probablement bloquée pendant une batterie faible. Le tile peut rester figé tant que la montre n'est pas rechargée.",
                hint = "Rechargez la montre puis relancez Vérifier la synchronisation.",
            )

            !hasSuccessfulSync -> SetupOverview(
                headline = "Première sync en attente",
                detail = "La configuration est presque prête. Lancez une vérification pour confirmer que le téléphone récupère bien une donnée Dexcom.",
                hint = "Utilisez Vérifier la synchronisation, puis installez le tile ou le widget quand tout est vert.",
            )

            !batteryProtected -> SetupOverview(
                headline = "Configuration prête",
                detail = "Dexcom et la montre sont bien reliés. Android peut encore retarder la sync en arrière-plan tant que la restriction batterie reste active.",
                hint = "Retirez la restriction batterie pour une sync plus régulière.",
            )

            else -> SetupOverview(
                headline = "Tout est prêt",
                detail = "Le téléphone récupère les données, la montre est reliée et la synchronisation est active.",
                hint = "Vous pouvez maintenant installer ou utiliser le tile, le widget et la complication en confiance.",
            )
        }
    }

    private fun buildWatchStatusLabel(
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
    ): String {
        val summary = watchHealth?.summary()
        return if (summary.isNullOrBlank()) {
            watchStatus.label()
        } else {
            "${watchStatus.label()} - $summary"
        }
    }

    private data class SetupOverview(
        val headline: String,
        val detail: String,
        val hint: String,
    )

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
