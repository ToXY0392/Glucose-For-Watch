package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

private const val PREVIEW_WIDTH_DP = 411
private const val PREVIEW_HEIGHT_DP = 891
private const val PREVIEW_BACKGROUND = 0xFFF7F9F7

@Preview(
    name = "HomeScreen - connected",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = PREVIEW_WIDTH_DP,
    heightDp = PREVIEW_HEIGHT_DP,
    backgroundColor = PREVIEW_BACKGROUND,
)
@Composable
internal fun HomeScreenConnectedConfigurationPreview() {
    val context = LocalContext.current
    ConfigurationScreenPreviewHost {
        HomeScreen(
            state = HomePreviewStates.connected(context),
            syncEnabled = true,
            onSyncClick = {},
            onDexcomClick = {},
            onWatchClick = {},
            onUnitClick = {},
            onBatteryClick = {},
            onPermissionsClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

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
                    username = "alice",
                    serverLabel = "Europe",
                    legalTermsAccepted = true,
                    medicalWarningAccepted = true,
                ),
            serverOptions = listOf("Europe", "US"),
            onUsernameChange = {},
            onPasswordChange = {},
            onServerChange = {},
            onLegalTermsChange = {},
            onMedicalWarningChange = {},
            onConnect = {},
            onDisconnect = {},
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
                    isLoggedIn = true,
                    connectedUsername = "alice",
                ),
            serverOptions = listOf("Europe", "US"),
            onUsernameChange = {},
            onPasswordChange = {},
            onServerChange = {},
            onLegalTermsChange = {},
            onMedicalWarningChange = {},
            onConnect = {},
            onDisconnect = {},
            onConfirmDisconnect = {},
            onDismissDisconnect = {},
            onDocumentClick = {},
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

@Preview(
    name = "CguScreen",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = PREVIEW_WIDTH_DP,
    heightDp = PREVIEW_HEIGHT_DP,
    backgroundColor = PREVIEW_BACKGROUND,
)
@Composable
internal fun CguScreenConfigurationPreview() {
    ConfigurationScreenPreviewHost {
        CguScreen(
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "PrivacyPolicyScreen",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = PREVIEW_WIDTH_DP,
    heightDp = PREVIEW_HEIGHT_DP,
    backgroundColor = PREVIEW_BACKGROUND,
)
@Composable
internal fun PrivacyPolicyScreenConfigurationPreview() {
    ConfigurationScreenPreviewHost {
        PrivacyPolicyScreen(
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "MedicalWarningScreen",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = PREVIEW_WIDTH_DP,
    heightDp = PREVIEW_HEIGHT_DP,
    backgroundColor = PREVIEW_BACKGROUND,
)
@Composable
internal fun MedicalWarningScreenConfigurationPreview() {
    ConfigurationScreenPreviewHost {
        MedicalWarningScreen(
            onBackClick = {},
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
