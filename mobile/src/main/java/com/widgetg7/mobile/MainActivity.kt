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
        syncStatusText.text = "Etat: actualisation en cours..."

        when (val result = PhoneGlucoseSyncEngine(this).run(triggeredFromWatch = false)) {
            is SyncExecutionResult.Success ->
                Log.d(logTag, "Manual sync completed source=${result.sourceName}")

            is SyncExecutionResult.Failure -> {
                syncStatusText.text = "Etat: ${result.message}"
                Log.d(logTag, "Manual sync failed message=${result.message}")
            }
        }

        refreshHome()
        refreshNowButton.isEnabled = true
        refreshNowButton.text = "Verifier la synchronisation"
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
                headline = "Dexcom a configurer",
                detail = "Le telephone ne peut pas alimenter le tile ni le widget tant que le compte Dexcom Share n'est pas configure.",
                hint = "Commencez par Configurer Dexcom, puis revenez ici pour verifier la montre.",
            )

            !watchStatus.connected -> SetupOverview(
                headline = "Montre a relier",
                detail = "Le compte Dexcom est pret, mais aucune montre connectee n'a ete detectee pour recevoir les mises a jour.",
                hint = "Ouvrez Configurer la montre puis ajoutez ensuite le tile ou la complication.",
            )

            watchHealth?.syncLimited == true -> SetupOverview(
                headline = "Sync montre limitee",
                detail = "La montre signale une sync probablement bloquee pendant une batterie faible. Le tile peut rester fige tant que la montre n'est pas rechargee.",
                hint = "Rechargez la montre puis relancez Verifier la synchronisation.",
            )

            !hasSuccessfulSync -> SetupOverview(
                headline = "Premiere sync en attente",
                detail = "La configuration est presque prete. Lancez une verification pour confirmer que le telephone recupere bien une donnee Dexcom.",
                hint = "Utilisez Verifier la synchronisation, puis installez le tile ou le widget quand tout est vert.",
            )

            !batteryProtected -> SetupOverview(
                headline = "Configuration prete",
                detail = "Dexcom et la montre sont bien relies. Android peut encore retarder la sync en arriere-plan tant que la restriction batterie reste active.",
                hint = "Retirez la restriction batterie pour une sync plus reguliere.",
            )

            else -> SetupOverview(
                headline = "Tout est pret",
                detail = "Le telephone recupere les donnees, la montre est reliee et la synchronisation est active.",
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
