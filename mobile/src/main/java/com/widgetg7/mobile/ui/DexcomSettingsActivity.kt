package com.widgetg7.mobile.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
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
import com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine
import com.widgetg7.mobile.sync.PhoneSyncStateStore
import com.widgetg7.mobile.sync.SyncErrorAdapter
import com.widgetg7.mobile.ui.compose.DexcomSettingsScreen
import com.widgetg7.mobile.ui.compose.DexcomSettingsUiState
import com.widgetg7.mobile.ui.theme.WidgetG7Theme
import kotlinx.coroutines.launch

/** Dexcom Share account editor and connection test. */
class DexcomSettingsActivity : ComponentActivity() {
    private val snackbarHostState = SnackbarHostState()
    private var uiState by mutableStateOf(DexcomSettingsUiState())

    private lateinit var settingsStore: AppSettingsStore
    private lateinit var launchStateStore: LaunchStateStore
    private lateinit var syncStatusRepository: SyncStatusRepository
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

        firstConnectionFlow = intent.getBooleanExtra(EXTRA_FIRST_CONNECTION_FLOW, false)
        settingsStore = AppSettingsStore(this)
        launchStateStore = LaunchStateStore(this)
        syncStatusRepository = SyncStatusRepository(this)

        val currentSettings = settingsStore.loadDexcomSettings()
        uiState =
            DexcomSettingsUiState(
                username = currentSettings.username,
                password = currentSettings.password,
                serverLabel = SyncServerLabelFormatter.displayServer(currentSettings.server),
                accountSummary =
                    SyncStatusTextFormatter.dexcomAccountSummary(
                        dexcomConfigured = currentSettings.isConfigured(),
                        serverLabel = SyncServerLabelFormatter.displayServer(currentSettings.server),
                        syncStatus = syncStatusRepository.load(),
                    ),
                statusMessage = getString(R.string.dexcom_no_verification_yet),
                saveButtonLabel = getString(R.string.dexcom_verify_connect),
            )

        enableEdgeToEdge()
        setContent {
            WidgetG7Theme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { padding ->
                    DexcomSettingsScreen(
                        state = uiState,
                        serverOptions =
                            listOf(
                                getString(R.string.dexcom_server_europe),
                                getString(R.string.dexcom_server_us),
                            ),
                        onUsernameChange = { uiState = uiState.copy(username = it) },
                        onPasswordChange = { uiState = uiState.copy(password = it) },
                        onServerChange = { uiState = uiState.copy(serverLabel = it) },
                        onSave = { saveAndVerify() },
                        onDisconnect = { disconnect() },
                        onBack = { finish() },
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .background(MaterialTheme.colorScheme.background),
                    )
                }
            }
        }
    }

    private fun saveAndVerify() {
        lifecycleScope.launch {
            setBusy(true)
            val settings = readSettingsFromState()
            uiState = uiState.copy(statusMessage = getString(R.string.dexcom_connect_in_progress))

            try {
                val config =
                    DexcomShareConfig(
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
                val statusMessage = connectionStatusMessage(syncResult)
                uiState =
                    uiState.copy(
                        accountSummary =
                            SyncStatusTextFormatter.dexcomAccountSummary(
                                dexcomConfigured = settings.isConfigured(),
                                serverLabel = SyncServerLabelFormatter.displayServer(settings.server),
                                syncStatus = syncStatusRepository.load(),
                            ),
                        statusMessage = statusMessage,
                    )
                snackbarHostState.showSnackbar(statusMessage)
                if (firstConnectionFlow) {
                    startActivity(
                        Intent(this@DexcomSettingsActivity, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        },
                    )
                    finish()
                }
            } catch (t: Throwable) {
                syncStatusRepository.saveError(
                    SyncErrorAdapter.toUserMessage(t),
                    SyncErrorAdapter.toCategory(t),
                )
                uiState =
                    uiState.copy(
                        accountSummary =
                            SyncStatusTextFormatter.dexcomAccountSummary(
                                dexcomConfigured = settings.isConfigured(),
                                serverLabel = SyncServerLabelFormatter.displayServer(settings.server),
                                syncStatus = syncStatusRepository.load(),
                            ),
                        statusMessage =
                            getString(
                                R.string.dexcom_connect_failed,
                                SyncErrorAdapter.toUserMessage(t),
                            ),
                    )
            }

            setBusy(false)
        }
    }

    private fun disconnect() {
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

    private fun readSettingsFromState(): DexcomUserSettings =
        DexcomUserSettings(
            username = uiState.username,
            password = uiState.password,
            server = toServerCode(uiState.serverLabel),
        )

    private fun setBusy(isBusy: Boolean) {
        uiState =
            uiState.copy(
                isBusy = isBusy,
                saveButtonLabel =
                    if (isBusy) {
                        getString(R.string.dexcom_verify_busy)
                    } else {
                        getString(R.string.dexcom_verify_connect)
                    },
            )
    }

    private fun toServerCode(label: String): String = if (label.equals("US", true)) "US" else "OUS"

    companion object {
        const val EXTRA_FIRST_CONNECTION_FLOW = "extra_first_connection_flow"
    }
}
