package com.widgetg7.mobile.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.widgetg7.feature.sync.SyncStatusRepository
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.LaunchStateStore
import com.widgetg7.mobile.settings.LegalConsentStore
import com.widgetg7.mobile.sync.ActiveGlucoseSyncController
import com.widgetg7.mobile.sync.PhoneSyncStateStore
import com.widgetg7.mobile.ui.compose.DexcomEntryScreen
import com.widgetg7.mobile.ui.compose.DexcomEntryUiState
import com.widgetg7.mobile.ui.theme.WidgetG7Theme

/** First-run Dexcom connection and legal consent gate. */
class DexcomEntryActivity : ComponentActivity() {
    private lateinit var appSettingsStore: AppSettingsStore
    private lateinit var launchStateStore: LaunchStateStore
    private lateinit var syncStatusRepository: SyncStatusRepository
    private lateinit var legalConsentStore: LegalConsentStore

    private var uiState by mutableStateOf(DexcomEntryUiState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appSettingsStore = AppSettingsStore(this)
        launchStateStore = LaunchStateStore(this)
        syncStatusRepository = SyncStatusRepository(this)
        legalConsentStore = LegalConsentStore(this)

        if (legalConsentStore.hasAcceptedCurrentVersion()) {
            uiState =
                uiState.copy(
                    legalTermsAccepted = true,
                    medicalWarningAccepted = true,
                )
        }
        refreshConfiguredState()

        enableEdgeToEdge()
        setContent {
            WidgetG7Theme {
                DexcomEntryScreen(
                    state = uiState,
                    onLegalTermsChange = { checked ->
                        uiState = uiState.copy(legalTermsAccepted = checked)
                    },
                    onMedicalWarningChange = { checked ->
                        uiState = uiState.copy(medicalWarningAccepted = checked)
                    },
                    onPrimaryAction = { onPrimaryActionClicked() },
                    onConfirmDisconnect = { performDisconnect() },
                    onDismissDisconnect = {
                        uiState = uiState.copy(showDisconnectDialog = false)
                    },
                    onDocumentClick = { documentType ->
                        startActivity(
                            Intent(this, LegalDocumentActivity::class.java).apply {
                                putExtra(LegalDocumentActivity.EXTRA_DOCUMENT_TYPE, documentType)
                            },
                        )
                    },
                    onBack = { finish() },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshConfiguredState()
    }

    private fun refreshConfiguredState() {
        uiState = uiState.copy(isConfigured = appSettingsStore.loadDexcomSettings().isConfigured())
    }

    private fun onPrimaryActionClicked() {
        if (appSettingsStore.loadDexcomSettings().isConfigured()) {
            uiState = uiState.copy(showDisconnectDialog = true)
        } else {
            legalConsentStore.markAcceptedCurrentVersion()
            startActivity(
                Intent(this, DexcomSettingsActivity::class.java).apply {
                    putExtra(DexcomSettingsActivity.EXTRA_FIRST_CONNECTION_FLOW, true)
                },
            )
        }
    }

    private fun performDisconnect() {
        appSettingsStore.clearDexcomSettings()
        ActiveGlucoseSyncController.stop(this)
        launchStateStore.resetDexcomEntry()
        legalConsentStore.clearAcceptedVersion()
        syncStatusRepository.clearSessionState()
        PhoneSyncStateStore(this).clear()
        uiState =
            uiState.copy(
                showDisconnectDialog = false,
                legalTermsAccepted = false,
                medicalWarningAccepted = false,
                isConfigured = false,
            )
    }
}
