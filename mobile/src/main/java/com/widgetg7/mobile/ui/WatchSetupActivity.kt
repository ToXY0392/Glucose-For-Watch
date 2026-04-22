package com.widgetg7.mobile.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.R
import com.widgetg7.mobile.data.PhoneGlucoseSourceFactory
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.sync.PhoneWearSyncService
import com.widgetg7.mobile.watch.ConnectedWatchNode
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchSyncHealthRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class WatchSetupActivity : AppCompatActivity() {
    private lateinit var refreshWatchStatusButton: Button
    private lateinit var watchStatusText: TextView
    private lateinit var watchSelectorLabel: TextView
    private lateinit var watchSelectorSpinner: Spinner

    private var connectedWatches: List<ConnectedWatchNode> = emptyList()
    private var applyingSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_setup)

        refreshWatchStatusButton = findViewById(R.id.refreshWatchStatusButton)
        watchStatusText = findViewById(R.id.watchSetupStatusText)
        watchSelectorLabel = findViewById(R.id.watchSelectorLabel)
        watchSelectorSpinner = findViewById(R.id.watchSelectorSpinner)

        refreshWatchStatusButton.setOnClickListener {
            refreshWatchStatus()
        }

        watchSelectorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (applyingSelection) return
                val selectedWatch = connectedWatches.getOrNull(position) ?: return
                WatchConnectionRepository(this@WatchSetupActivity).savePreferredWatch(selectedWatch)
                lifecycleScope.launch {
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
        refreshWatchStatusButton.isEnabled = false
        refreshWatchStatusButton.text = "Test en cours..."
        watchStatusText.text = "Verification de la liaison montre..."

        lifecycleScope.launch {
            refreshWatchChoices()
            watchStatusText.text = runWatchVerification()
            refreshWatchStatusButton.isEnabled = true
            refreshWatchStatusButton.text = "Tester la liaison montre"
        }
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

    private suspend fun runWatchVerification(): String {
        val status = WatchConnectionRepository(this).loadStatus()
        val watchHealth = WatchSyncHealthRepository(this).load()
        val healthSummary = watchHealth?.summary()

        if (!status.connected) {
            return listOfNotNull(
                "Aucune montre detectee.",
                healthSummary,
            ).joinToString("\n")
        }

        val preferredNote = if (status.connectedWatches.size > 1) {
            "Montre selectionnee : ${status.displayName}."
        } else {
            null
        }

        if (!AppSettingsStore(this).loadDexcomSettings().isConfigured()) {
            return listOfNotNull(
                status.label(),
                preferredNote,
                "La liaison telephone - montre est OK.",
                "Le test complet demande une connexion Dexcom active.",
                healthSummary,
            ).joinToString("\n")
        }

        return try {
            val reading = withTimeout(VERIFY_TIMEOUT_MS) {
                PhoneGlucoseSourceFactory.create(this@WatchSetupActivity).latest()
            }
            withTimeout(VERIFY_TIMEOUT_MS) {
                PhoneWearSyncService(this@WatchSetupActivity).pushLatest(reading)
            }

            listOfNotNull(
                status.label(),
                preferredNote,
                "Test complet reussi : la derniere glycemie a ete envoyee a la montre.",
                "Valeur testee : ${reading.valueMgDl} mg/dL ${reading.trend}.",
                healthSummary,
            ).joinToString("\n")
        } catch (_: TimeoutCancellationException) {
            listOfNotNull(
                status.label(),
                preferredNote,
                "La liaison est detectee, mais le test complet a expire.",
                "Verifiez Dexcom, le Bluetooth et la montre, puis recommencez.",
                healthSummary,
            ).joinToString("\n")
        } catch (error: Throwable) {
            listOfNotNull(
                status.label(),
                preferredNote,
                "La liaison est detectee, mais l'envoi de la glycemie a echoue.",
                error.message?.takeIf { it.isNotBlank() },
                healthSummary,
            ).joinToString("\n")
        }
    }

    companion object {
        private const val VERIFY_TIMEOUT_MS = 12_000L
    }
}
