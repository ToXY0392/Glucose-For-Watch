package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glucoseforwatch.mobile.R

data class WatchSetupUiState(
    val showWatchSelector: Boolean = false,
    val watchNames: List<String> = emptyList(),
    val selectedWatchIndex: Int = 0,
    val batteryButtonLabel: String = "",
    val batteryButtonEnabled: Boolean = true,
    val watchTestEnabled: Boolean = false,
    val watchTestLabel: String = "",
    val bluetoothStatusLabel: String = "",
    val isBluetoothConnected: Boolean = false,
    val wearAppStatusLabel: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchSetupScreen(
    state: WatchSetupUiState,
    onWatchSelected: (Int) -> Unit,
    onBatteryOptimization: () -> Unit,
    onInstallWear: () -> Unit,
    onWatchTest: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var watchMenuExpanded by remember { mutableStateOf(false) }
    val selectedWatchLabel = state.watchNames.getOrNull(state.selectedWatchIndex).orEmpty()
    val bluetoothTitle =
        state.bluetoothStatusLabel.ifBlank {
            stringResource(R.string.home_companion_setting_watch_title)
        }
    val bluetoothSubtitle =
        stringResource(
            if (state.isBluetoothConnected) {
                R.string.home_companion_connected
            } else {
                R.string.home_companion_disconnected
            },
        )

    SecondaryScreenScaffold(
        title = stringResource(R.string.watch_setup_title),
        subtitle = stringResource(R.string.watch_setup_options_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        CompanionGroupedCard(modifier = Modifier.padding(top = 24.dp)) {
            CompanionListItem(
                icon = R.drawable.ic_bluetooth_24,
                title = bluetoothTitle,
                subtitle = bluetoothSubtitle,
            )
            if (state.wearAppStatusLabel.isNotBlank()) {
                CompanionCardDivider()
                CompanionListItem(
                    icon = R.drawable.ic_watch_24,
                    title = stringResource(R.string.home_companion_setting_watch_title),
                    subtitle = state.wearAppStatusLabel,
                )
            }
            if (state.showWatchSelector) {
                CompanionCardDivider()
                ExposedDropdownMenuBox(
                    expanded = watchMenuExpanded,
                    onExpandedChange = { watchMenuExpanded = it },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    OutlinedTextField(
                        value = selectedWatchLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.watch_setup_primary_watch)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = watchMenuExpanded) },
                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = watchMenuExpanded,
                        onDismissRequest = { watchMenuExpanded = false },
                    ) {
                        state.watchNames.forEachIndexed { index, name ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    onWatchSelected(index)
                                    watchMenuExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onInstallWear,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
        ) {
            Text(stringResource(R.string.watch_setup_install_wear))
        }

        CompanionGroupedCard(modifier = Modifier.padding(top = 16.dp)) {
            CompanionListItem(
                icon = R.drawable.ic_battery_saver_24,
                title = stringResource(R.string.home_companion_setting_battery_title),
                subtitle = state.batteryButtonLabel,
                onClick = if (state.batteryButtonEnabled) onBatteryOptimization else null,
            )
            CompanionCardDivider()
            CompanionListItem(
                icon = R.drawable.ic_refresh_24,
                title = stringResource(R.string.home_watch_test),
                subtitle = state.watchTestLabel,
                onClick = if (state.watchTestEnabled) onWatchTest else null,
            )
        }
    }
}
