package com.widgetg7.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Spinner
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.R
import com.widgetg7.mobile.battery.BatteryOptimizationHelper
import com.widgetg7.mobile.data.PhoneGlucoseSourceFactory
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.sync.PhoneWearSyncService
import com.widgetg7.mobile.watch.ConnectedWatchNode
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchSyncHealthRepository
import com.widgetg7.mobile.watch.WatchStatusVerifier
import com.widgetg7.mobile.watch.WatchVisualResolver
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class WatchSetupActivity : AppCompatActivity() {
    private var baseScrollPaddingTop = 0

    private lateinit var testWatchSyncButton: Button
    private lateinit var batteryOptimizationButton: Button
    private lateinit var backToHomeButton: ImageButton
    private lateinit var watchStatusHeadlineText: TextView
    private lateinit var watchStatusSupportText: TextView
    private lateinit var watchStatusText: TextView
    private lateinit var watchSelectorLabel: TextView
    private lateinit var watchSelectorSpinner: Spinner
    private lateinit var watchHeroImage: ImageView

    private var connectedWatches: List<ConnectedWatchNode> = emptyList()
    private var applyingSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_setup)

        val scrollView = findViewById<ScrollView>(R.id.watchSetupScrollView)
        baseScrollPaddingTop = scrollView.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                baseScrollPaddingTop + systemBarsInsets.top,
                view.paddingRight,
                view.paddingBottom,
            )
            insets
        }
        ViewCompat.requestApplyInsets(scrollView)

        testWatchSyncButton = findViewById(R.id.testWatchSyncButton)
        findViewById<MaterialButton>(R.id.openWearInstallerButton).setOnClickListener {
            startActivity(Intent(this, WearInstallerActivity::class.java))
        }
        batteryOptimizationButton = findViewById(R.id.batteryOptimizationButton)
        backToHomeButton = findViewById(R.id.backToHomeButton)
        watchStatusHeadlineText = findViewById(R.id.watchStatusHeadlineText)
        watchStatusSupportText = findViewById(R.id.watchStatusSupportText)
        watchStatusText = findViewById(R.id.watchSetupStatusText)
        watchSelectorLabel = findViewById(R.id.watchSelectorLabel)
        watchSelectorSpinner = findViewById(R.id.watchSelectorSpinner)
        watchHeroImage = findViewById(R.id.watchHeroImage)

        testWatchSyncButton.setOnClickListener {
            refreshWatchStatus()
        }
        batteryOptimizationButton.setOnClickListener {
            runCatching {
                startActivity(BatteryOptimizationHelper(this).buildSettingsIntent())
            }
        }
        backToHomeButton.setOnClickListener { finish() }

        watchSelectorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (applyingSelection) return
                val selectedWatch = connectedWatches.getOrNull(position) ?: return
                WatchConnectionRepository(this@WatchSetupActivity).savePreferredWatch(selectedWatch)
                lifecycleScope.launch {
                    refreshHeaderVisuals()
                    watchStatusText.text = runWatchVerification()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        refreshWatchStatus()
    }

    override fun onResume() {
        super.onResume()
        refreshWatchStatus()
    }

    private fun refreshWatchStatus() {
        testWatchSyncButton.isEnabled = false
        testWatchSyncButton.text = "Test en cours..."
        watchStatusText.text = "Vérification de la liaison montre..."

        lifecycleScope.launch {
            refreshWatchChoices()
            refreshHeaderVisuals()
            refreshBatteryOptimizationStatus()
            watchStatusText.text = runWatchVerification()
            testWatchSyncButton.isEnabled = true
            testWatchSyncButton.text = "Tester l'envoi"
        }
    }

    private fun refreshBatteryOptimizationStatus() {
        val status = BatteryOptimizationHelper(this).loadStatus()
        batteryOptimizationButton.text = if (status.isProtectedFromOptimization) {
            "Sync en veille autorisee"
        } else {
            "Autoriser la sync en veille"
        }
        batteryOptimizationButton.isEnabled = !status.isProtectedFromOptimization
    }


    private suspend fun refreshWatchChoices() {
        val repository = WatchConnectionRepository(this)
        connectedWatches = repository.loadConnectedWatches()

        val showSelector = connectedWatches.size > 1
        watchSelectorLabel.visibility = if (showSelector) View.VISIBLE else View.GONE
        watchSelectorSpinner.visibility = if (showSelector) View.VISIBLE else View.GONE

        if (!showSelector) {
            return
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            connectedWatches.map { it.displayName },
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        watchSelectorSpinner.adapter = adapter

        val preferredNodeId = repository.loadPreferredWatchId()
        val selectedIndex = connectedWatches.indexOfFirst { it.nodeId == preferredNodeId }.takeIf { it >= 0 } ?: 0

        applyingSelection = true
        watchSelectorSpinner.setSelection(selectedIndex, false)
        applyingSelection = false
    }

    private suspend fun refreshHeaderVisuals() {
        val status = WatchConnectionRepository(this).loadStatus()
        val health = WatchSyncHealthRepository(this).load()
        val visual = WatchVisualResolver.resolve(status.displayName, health)

        watchHeroImage.setImageResource(visual.drawableResId)
        watchStatusHeadlineText.text = when {
            !status.connected -> "Aucune montre connectée"
            status.connectedWatches.size > 1 -> "Montre principale : ${visual.headline}"
            else -> "Montre connectée : ${visual.headline}"
        }

        watchStatusSupportText.text = buildList {
            if (!status.connected) {
                add("Connectez une montre Wear OS au téléphone.")
            } else {
                add(visual.supportLabel ?: "Liaison détectée")
                if (status.preferredNodeMissing) {
                    add("La montre principale précédente n'est plus détectée.")
                }
                health?.summary(this@WatchSetupActivity)?.let {
                    add(
                        it.removePrefix("Montre: ").replaceFirstChar { char ->
                            if (char.isLowerCase()) char.titlecase() else char.toString()
                        },
                    )
                }
            }
        }.joinToString(" ")
    }

    private suspend fun runWatchVerification(): String {
        val status = WatchConnectionRepository(this).loadStatus()
        val watchHealth = WatchSyncHealthRepository(this).load()
        val healthSummary = watchHealth?.summary(this)

        if (!status.connected) {
            return listOfNotNull(
                "Aucune montre détectée.",
                healthSummary,
            ).joinToString("\n")
        }

        val wearStatus = runCatching {
            WatchStatusVerifier(this).requestStatus(status.nodeId)
        }.getOrNull()
        val wearInstallSummary = when {
            wearStatus?.appInstalled == true && wearStatus.supportsTile && wearStatus.supportsComplication ->
                getString(R.string.watch_setup_wear_ready_all)
            wearStatus?.appInstalled == true && (!wearStatus.supportsTile || !wearStatus.supportsComplication) ->
                getString(R.string.watch_setup_wear_ready_partial)
            wearStatus?.appInstalled == true ->
                getString(R.string.watch_setup_wear_ready_app)
            else ->
                getString(R.string.watch_setup_wear_not_ready)
        }
        val latestHealthSummary = wearStatus?.summary(this) ?: healthSummary

        val preferredNote = if (status.connectedWatches.size > 1) {
            "Montre sélectionnée : ${status.displayName}."
        } else {
            null
        }

        if (!AppSettingsStore(this).loadDexcomSettings().isConfigured()) {
            return listOfNotNull(
                status.label(),
                preferredNote,
                wearInstallSummary,
                "La liaison téléphone - montre est opérationnelle.",
                "Le test complet nécessite une connexion Dexcom active.",
                latestHealthSummary,
            ).joinToString("\n")
        }

        return try {
            val reading = withTimeout(VERIFY_TIMEOUT_MS) {
                PhoneGlucoseSourceFactory.create(this@WatchSetupActivity).latest()
            }
            val sendOk =
                withTimeout(VERIFY_TIMEOUT_MS) {
                    val stateStore = com.widgetg7.mobile.sync.PhoneSyncStateStore(this@WatchSetupActivity)
                    val sequenceId = stateStore.nextSequenceId()
                    val ok =
                        PhoneWearSyncService(this@WatchSetupActivity).pushLatest(reading, sequenceId)
                    if (ok) {
                        stateStore.recordPushSuccess(
                            timestampEpochMs = reading.timestampEpochMs,
                            sequenceId = sequenceId,
                            valueMgDl = reading.valueMgDl,
                            trend = reading.trend,
                            deltaMgDl = reading.deltaMgDl,
                            stale = reading.stale,
                        )
                    }
                    ok
                }

            listOfNotNull(
                status.label(),
                preferredNote,
                wearInstallSummary,
                if (sendOk) {
                    "Test complet réussi : la dernière glycémie a été envoyée à la montre."
                } else {
                    "Dexcom OK, mais la montre n’a pas reçu la donnée (vérifiez Bluetooth / Wear OS puis recommencez)."
                },
                "Valeur testée : ${reading.valueMgDl} mg/dL ${reading.trend}.",
                latestHealthSummary,
            ).joinToString("\n")
        } catch (_: TimeoutCancellationException) {
            listOfNotNull(
                status.label(),
                preferredNote,
                wearInstallSummary,
                "La liaison est détectée, mais le test complet a expiré.",
                "Vérifiez Dexcom, le Bluetooth et la montre, puis recommencez.",
                latestHealthSummary,
            ).joinToString("\n")
        } catch (error: Throwable) {
            listOfNotNull(
                status.label(),
                preferredNote,
                wearInstallSummary,
                "La liaison est détectée, mais l'envoi de la glycémie a échoué.",
                error.message?.takeIf { it.isNotBlank() },
                latestHealthSummary,
            ).joinToString("\n")
        }
    }

    companion object {
        private const val VERIFY_TIMEOUT_MS = 12_000L
    }
}
