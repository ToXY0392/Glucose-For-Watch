package com.glucoseforwatch.mobile.ui

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
import com.glucoseforwatch.feature.dexcomshare.DexcomShareClient
import com.glucoseforwatch.feature.dexcomshare.DexcomShareConfig
import com.glucoseforwatch.feature.sync.SyncExecutionResult
import com.glucoseforwatch.feature.sync.SyncServerLabelFormatter
import com.glucoseforwatch.feature.sync.SyncStatusRepository
import com.glucoseforwatch.feature.sync.WatchDeliveryStatus
import com.glucoseforwatch.mobile.BuildConfig
import com.glucoseforwatch.mobile.MainActivity
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.settings.AppSettingsStore
import com.glucoseforwatch.mobile.settings.DexcomUserSettings
import com.glucoseforwatch.mobile.settings.LaunchStateStore
import com.glucoseforwatch.mobile.settings.LegalConsentStore
import com.glucoseforwatch.mobile.sync.ActiveGlucoseSyncController
import com.glucoseforwatch.mobile.sync.PhoneGlucoseSyncEngine
import com.glucoseforwatch.mobile.sync.PhoneSyncStateStore
import com.glucoseforwatch.mobile.sync.SyncErrorAdapter
import com.glucoseforwatch.mobile.ui.compose.DexcomEntryScreen
import com.glucoseforwatch.mobile.ui.compose.DexcomEntryUiState
import com.glucoseforwatch.mobile.ui.compose.DexcomLegalDocumentType
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme
import kotlinx.coroutines.launch

/** Dexcom Share connection, legal consent, and account management. */
class DexcomEntryActivity : ComponentActivity() {
    private val snackbarHostState = SnackbarHostState()

    private lateinit var appSettingsStore: AppSettingsStore
    private lateinit var launchStateStore: LaunchStateStore
    private lateinit var syncStatusRepository: SyncStatusRepository
    private lateinit var legalConsentStore: LegalConsentStore

    private var firstConnectionFlow = false
    private var uiState by mutableStateOf(DexcomEntryUiState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appSettingsStore = AppSettingsStore(this)
        launchStateStore = LaunchStateStore(this)
        syncStatusRepository = SyncStatusRepository(this)
        legalConsentStore = LegalConsentStore(this)
        firstConnectionFlow = !launchStateStore.hasCompletedDexcomEntry()

        refreshState()

        enableEdgeToEdge()
        setContent {
            GlucoseForWatchTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { padding ->
                    DexcomEntryScreen(
                        state = uiState,
                        serverOptions =
                            listOf(
                                getString(R.string.dexcom_server_europe),
                                getString(R.string.dexcom_server_us),
                            ),
                        onUsernameChange = { uiState = uiState.copy(username = it) },
                        onPasswordChange = { uiState = uiState.copy(password = it) },
                        onServerChange = { uiState = uiState.copy(serverLabel = it) },
                        onLegalTermsChange = { checked ->
                            uiState = uiState.copy(legalTermsAccepted = checked)
                        },
                        onMedicalWarningChange = { checked ->
                            uiState = uiState.copy(medicalWarningAccepted = checked)
                        },
                        onConnect = { connect() },
                        onDisconnect = { uiState = uiState.copy(showDisconnectDialog = true) },
                        onConfirmDisconnect = { performDisconnect() },
                        onDismissDisconnect = {
                            uiState = uiState.copy(showDisconnectDialog = false)
                        },
                        onDocumentClick = { documentType ->
                            when (documentType) {
                                DexcomLegalDocumentType.CGU ->
                                    startActivity(Intent(this, CguActivity::class.java))
                                DexcomLegalDocumentType.PRIVACY ->
                                    startActivity(Intent(this, PrivacyPolicyActivity::class.java))
                                DexcomLegalDocumentType.MEDICAL ->
                                    startActivity(Intent(this, MedicalWarningActivity::class.java))
                            }
                        },
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

    override fun onResume() {
        super.onResume()
        refreshState()
    }

    private fun refreshState() {
        val settings = appSettingsStore.loadDexcomSettings()
        val isLoggedIn = settings.isConfigured()
        val legalAccepted = legalConsentStore.hasAcceptedCurrentVersion()

        uiState =
            uiState.copy(
                isLoggedIn = isLoggedIn,
                connectedUsername = if (isLoggedIn) settings.username else "",
                username = if (isLoggedIn) uiState.username else settings.username,
                password = if (isLoggedIn) "" else settings.password,
                serverLabel =
                    if (isLoggedIn) {
                        uiState.serverLabel
                    } else {
                        SyncServerLabelFormatter.displayServer(settings.server).ifBlank {
                            getString(R.string.dexcom_server_europe)
                        }
                    },
                legalTermsAccepted = legalAccepted || uiState.legalTermsAccepted,
                medicalWarningAccepted = legalAccepted || uiState.medicalWarningAccepted,
                connectButtonLabel =
                    uiState.connectButtonLabel.ifBlank {
                        getString(R.string.dexcom_entry_connect)
                    },
            )
    }

    private fun connect() {
        legalConsentStore.markAcceptedCurrentVersion()
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
                appSettingsStore.saveDexcomSettings(settings)
                appSettingsStore.setActiveSyncEnabled(true)
                launchStateStore.markDexcomEntryCompleted()
                syncStatusRepository.saveFetchedReading("dexcom-share", reading)
                val syncResult =
                    PhoneGlucoseSyncEngine(this@DexcomEntryActivity).run(
                        triggeredFromWatch = false,
                        forcePushCurrentReading = true,
                    )
                if (!firstConnectionFlow) {
                    ActiveGlucoseSyncController.start(this@DexcomEntryActivity)
                }
                val statusMessage = connectionStatusMessage(syncResult)
                uiState =
                    uiState.copy(
                        isLoggedIn = true,
                        connectedUsername = settings.username,
                        statusMessage = "",
                        showDisconnectDialog = false,
                    )
                snackbarHostState.showSnackbar(statusMessage)
                if (firstConnectionFlow) {
                    startActivity(
                        Intent(this@DexcomEntryActivity, MainActivity::class.java).apply {
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

    private fun performDisconnect() {
        appSettingsStore.clearDexcomSettings()
        ActiveGlucoseSyncController.stop(this)
        launchStateStore.resetDexcomEntry()
        legalConsentStore.clearAcceptedVersion()
        syncStatusRepository.clearSessionState()
        PhoneSyncStateStore(this).clear()
        firstConnectionFlow = true
        uiState =
            DexcomEntryUiState(
                serverLabel = getString(R.string.dexcom_server_europe),
                connectButtonLabel = getString(R.string.dexcom_entry_connect),
            )
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
            username = uiState.username.trim(),
            password = uiState.password.trim(),
            server = toServerCode(uiState.serverLabel.trim()),
        )

    private fun setBusy(isBusy: Boolean) {
        uiState =
            uiState.copy(
                isBusy = isBusy,
                connectButtonLabel =
                    if (isBusy) {
                        getString(R.string.dexcom_verify_busy)
                    } else {
                        getString(R.string.dexcom_entry_connect)
                    },
            )
    }

    private fun toServerCode(label: String): String = if (label.equals("US", true)) "US" else "OUS"
}
