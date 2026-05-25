package com.widgetg7.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.widgetg7.core.model.SyncStatusSnapshot
import com.widgetg7.feature.dexcomshare.DexcomShareClient
import com.widgetg7.feature.dexcomshare.DexcomShareConfig
import com.widgetg7.feature.sync.SyncExecutionResult
import com.widgetg7.feature.sync.SyncServerLabelFormatter
import com.widgetg7.feature.sync.SyncStatusRepository
import com.widgetg7.feature.sync.SyncStatusTextFormatter
import com.widgetg7.feature.sync.WatchDeliveryStatus
import com.widgetg7.mobile.BuildConfig
import com.widgetg7.mobile.MainActivity
import com.widgetg7.mobile.R
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.DexcomUserSettings
import com.widgetg7.mobile.settings.LaunchStateStore
import com.widgetg7.mobile.settings.LegalConsentStore
import com.widgetg7.mobile.sync.ActiveGlucoseSyncController
import com.widgetg7.mobile.sync.SyncErrorAdapter
import com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine
import com.widgetg7.mobile.sync.PhoneSyncStateStore
import kotlinx.coroutines.launch

class DexcomSettingsActivity : AppCompatActivity() {
    private lateinit var saveDexcomButton: Button
    private lateinit var disconnectDexcomButton: Button
    private lateinit var backIconButton: ImageView
    private var firstConnectionFlow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!LegalConsentStore(this).hasAcceptedCurrentVersion()) {
            startActivity(
                Intent(this, DexcomEntryActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
            )
            finish()
            return
        }
        setContentView(R.layout.activity_dexcom_settings)
        firstConnectionFlow = intent.getBooleanExtra(EXTRA_FIRST_CONNECTION_FLOW, false)

        val settingsStore = AppSettingsStore(this)
        val launchStateStore = LaunchStateStore(this)
        val syncStatusRepository = SyncStatusRepository(this)
        val currentSettings = settingsStore.loadDexcomSettings()

        val usernameInput = findViewById<EditText>(R.id.dexcomUsernameInput)
        val passwordInput = findViewById<EditText>(R.id.dexcomPasswordInput)
        val serverInput = findViewById<AutoCompleteTextView>(R.id.dexcomServerInput)
        val statusText = findViewById<TextView>(R.id.dexcomSettingsStatusText)
        val accountSummaryText = findViewById<TextView>(R.id.dexcomAccountSummaryText)
        saveDexcomButton = findViewById(R.id.saveDexcomButton)
        disconnectDexcomButton = findViewById(R.id.disconnectDexcomButton)
        backIconButton = findViewById(R.id.backIconButton)

        usernameInput.setText(currentSettings.username)
        passwordInput.setText(currentSettings.password)
        serverInput.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                listOf(
                    getString(R.string.dexcom_server_europe),
                    getString(R.string.dexcom_server_us),
                ),
            ),
        )
        serverInput.setText(SyncServerLabelFormatter.displayServer(currentSettings.server), false)

        renderAccountSummary(currentSettings, syncStatusRepository.load(), accountSummaryText, statusText)

        backIconButton.setOnClickListener { finish() }

        saveDexcomButton.setOnClickListener {
            lifecycleScope.launch {
                setBusyState(true)
                val settings = readSettings(usernameInput, passwordInput, serverInput)

                statusText.text = getString(R.string.dexcom_connect_in_progress)

                try {
                    val config = DexcomShareConfig(
                        username = settings.username,
                        password = settings.password,
                        server = settings.server,
                        applicationId = BuildConfig.DEXCOM_SHARE_APPLICATION_ID.trim(),
                    )
                    val reading = DexcomShareClient(config).latest()
                    settingsStore.saveDexcomSettings(settings)
                    settingsStore.setActiveSyncEnabled(true)
                    launchStateStore.markDexcomEntryCompleted()
                    syncStatusRepository.saveFetchedReading("dexcom-share", reading)
                    ActiveGlucoseSyncController.start(this@DexcomSettingsActivity)
                    val syncResult = PhoneGlucoseSyncEngine(this@DexcomSettingsActivity).run(
                        triggeredFromWatch = false,
                        forcePushCurrentReading = true,
                    )
                    renderAccountSummary(settings, syncStatusRepository.load(), accountSummaryText, statusText)
                    statusText.text = connectionStatusMessage(syncResult)
                    Snackbar.make(findViewById(android.R.id.content), statusText.text, 2500).show()
                    if (firstConnectionFlow) {
                        startActivity(
                            Intent(this@DexcomSettingsActivity, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            },
                        )
                        finish()
                    }
                } catch (t: Throwable) {
                    syncStatusRepository.saveError(SyncErrorAdapter.toUserMessage(t), SyncErrorAdapter.toCategory(t))
                    renderAccountSummary(settings, syncStatusRepository.load(), accountSummaryText, statusText)
                    statusText.text = getString(R.string.dexcom_connect_failed, SyncErrorAdapter.toUserMessage(t))
                }

                setBusyState(false)
            }
        }

        disconnectDexcomButton.setOnClickListener {
            settingsStore.clearDexcomSettings()
            ActiveGlucoseSyncController.stop(this)
            launchStateStore.resetDexcomEntry()
            LegalConsentStore(this).clearAcceptedVersion()
            syncStatusRepository.clearSessionState()
            PhoneSyncStateStore(this).clear()
            startActivity(
                Intent(this, DexcomEntryActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                },
            )
            finish()
        }
    }

    private fun connectionStatusMessage(syncResult: SyncExecutionResult): String =
        when (syncResult) {
            is SyncExecutionResult.Failure ->
                getString(R.string.dexcom_connect_watch_failed, syncResult.message)

            is SyncExecutionResult.SuccessNewReading,
            is SyncExecutionResult.SuccessNoNewReading,
            ->
                when (syncResult.watchDelivery) {
                    WatchDeliveryStatus.DELIVERED ->
                        if (syncResult is SyncExecutionResult.SuccessNewReading) {
                            getString(R.string.dexcom_connect_success_sent)
                        } else {
                            getString(R.string.dexcom_connect_success_resent)
                        }

                    WatchDeliveryStatus.QUEUED,
                    WatchDeliveryStatus.WATCH_UNAVAILABLE,
                    -> getString(R.string.dexcom_connect_success_watch_pending)

                    WatchDeliveryStatus.NOT_APPLICABLE ->
                        getString(R.string.dexcom_connect_success)
                }
        }

    private fun readSettings(
        usernameInput: EditText,
        passwordInput: EditText,
        serverInput: AutoCompleteTextView,
    ): DexcomUserSettings {
        return DexcomUserSettings(
            username = usernameInput.text.toString(),
            password = passwordInput.text.toString(),
            server = toServerCode(serverInput.text.toString()),
        )
    }

    private fun renderAccountSummary(
        settings: DexcomUserSettings,
        syncStatus: SyncStatusSnapshot,
        accountSummaryText: TextView,
        statusText: TextView,
    ) {
        accountSummaryText.text = SyncStatusTextFormatter.dexcomAccountSummary(
            dexcomConfigured = settings.isConfigured(),
            serverLabel = SyncServerLabelFormatter.displayServer(settings.server),
            syncStatus = syncStatus,
        )

        if (statusText.text.isNullOrBlank()) {
            statusText.text = getString(R.string.dexcom_no_verification_yet)
        }
    }

    private fun setBusyState(isBusy: Boolean) {
        saveDexcomButton.isEnabled = !isBusy
        disconnectDexcomButton.isEnabled = !isBusy
        saveDexcomButton.text = if (isBusy) {
            getString(R.string.dexcom_verify_busy)
        } else {
            getString(R.string.dexcom_verify_connect)
        }
    }

    private fun toServerCode(label: String): String = if (label.equals("US", true)) "US" else "OUS"

    companion object {
        const val EXTRA_FIRST_CONNECTION_FLOW = "extra_first_connection_flow"
    }
}
