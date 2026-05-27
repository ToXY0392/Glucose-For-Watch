package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.watch_setup_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (state.showWatchSelector) {
            Text(
                text = stringResource(R.string.watch_setup_primary_watch),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 20.dp),
            )
            ExposedDropdownMenuBox(
                expanded = watchMenuExpanded,
                onExpandedChange = { watchMenuExpanded = it },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
            ) {
                OutlinedTextField(
                    value = selectedWatchLabel,
                    onValueChange = {},
                    readOnly = true,
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
        OutlinedButton(
            onClick = onBatteryOptimization,
            enabled = state.batteryButtonEnabled,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
        ) {
            Text(state.batteryButtonLabel)
        }
        OutlinedButton(
            onClick = onInstallWear,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
        ) {
            Text(stringResource(R.string.watch_setup_install_wear))
        }
        OutlinedButton(
            onClick = onWatchTest,
            enabled = state.watchTestEnabled,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
        ) {
            Text(state.watchTestLabel)
        }
        RoundBackButton(
            onClick = onBack,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp),
        )
    }
}
