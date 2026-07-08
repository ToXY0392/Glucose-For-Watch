package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

private const val PREVIEW_WIDTH_DP = 411
private const val PREVIEW_HEIGHT_DP = 891
private const val PREVIEW_BACKGROUND = 0xFFF1EEE9

@Preview(
    name = "DexcomEntryScreen - first connect",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = PREVIEW_WIDTH_DP,
    heightDp = PREVIEW_HEIGHT_DP,
    backgroundColor = PREVIEW_BACKGROUND,
)
@Composable
internal fun DexcomEntryScreenFirstConnectPreview() {
    ConfigurationScreenPreviewHost {
        DexcomEntryScreen(
            state =
                DexcomEntryUiState(
                    legalTermsAccepted = true,
                    medicalWarningAccepted = true,
                    isConfigured = false,
                ),
            onLegalTermsChange = {},
            onMedicalWarningChange = {},
            onPrimaryAction = {},
            onConfirmDisconnect = {},
            onDismissDisconnect = {},
            onDocumentClick = {},
            onBack = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "DexcomEntryScreen - configured",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = PREVIEW_WIDTH_DP,
    heightDp = PREVIEW_HEIGHT_DP,
    backgroundColor = PREVIEW_BACKGROUND,
)
@Composable
internal fun DexcomEntryScreenConfiguredPreview() {
    ConfigurationScreenPreviewHost {
        DexcomEntryScreen(
            state =
                DexcomEntryUiState(
                    legalTermsAccepted = true,
                    medicalWarningAccepted = true,
                    isConfigured = true,
                ),
            onLegalTermsChange = {},
            onMedicalWarningChange = {},
            onPrimaryAction = {},
            onConfirmDisconnect = {},
            onDismissDisconnect = {},
            onDocumentClick = {},
            onBack = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "DexcomSettingsScreen",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = PREVIEW_WIDTH_DP,
    heightDp = PREVIEW_HEIGHT_DP,
    backgroundColor = PREVIEW_BACKGROUND,
)
@Composable
internal fun DexcomSettingsScreenPreview() {
    ConfigurationScreenPreviewHost {
        DexcomSettingsScreen(
            state =
                DexcomSettingsUiState(
                    username = "alice",
                    password = "",
                    serverLabel = "Europe / OUS",
                    accountSummary = "Dexcom Share · Europe / OUS",
                    statusMessage = "Derniere sync il y a 2 min",
                    saveButtonLabel = "Verifier et enregistrer",
                ),
            serverOptions = listOf("Europe / OUS", "US"),
            onUsernameChange = {},
            onPasswordChange = {},
            onServerChange = {},
            onSave = {},
            onDisconnect = {},
            onBack = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "WatchSetupScreen - connected",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = PREVIEW_WIDTH_DP,
    heightDp = PREVIEW_HEIGHT_DP,
    backgroundColor = PREVIEW_BACKGROUND,
)
@Composable
internal fun WatchSetupScreenConnectedPreview() {
    ConfigurationScreenPreviewHost {
        WatchSetupScreen(
            state =
                WatchSetupUiState(
                    showWatchSelector = true,
                    watchNames = listOf("Pixel Watch 2", "Galaxy Watch 6"),
                    selectedWatchIndex = 0,
                    batteryButtonLabel = "Autoriser la sync en veille",
                    batteryButtonEnabled = true,
                    watchTestEnabled = true,
                    watchTestLabel = "Envoyer une lecture test",
                    bluetoothStatusLabel = "Pixel Watch 2",
                    isBluetoothConnected = true,
                    wearAppStatusLabel = "Glucose For Watch est installe sur la montre.",
                ),
            onWatchSelected = {},
            onBatteryOptimization = {},
            onInstallWear = {},
            onWatchTest = {},
            onBack = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "GlucoseUnitSettingsScreen",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = PREVIEW_WIDTH_DP,
    heightDp = PREVIEW_HEIGHT_DP,
    backgroundColor = PREVIEW_BACKGROUND,
)
@Composable
internal fun GlucoseUnitSettingsScreenPreview() {
    ConfigurationScreenPreviewHost {
        GlucoseUnitSettingsScreen(
            selectedUnit = GlucoseDisplayUnit.MG_DL,
            onUnitSelected = {},
            onBack = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
internal fun ConfigurationScreenPreviewHost(content: @Composable () -> Unit) {
    GlucoseForWatchTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            content()
        }
    }
}
