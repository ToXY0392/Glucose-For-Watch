package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.glucoseforwatch.mobile.R

data class DexcomSettingsUiState(
    val username: String = "",
    val password: String = "",
    val serverLabel: String = "",
    val accountSummary: String = "",
    val statusMessage: String = "",
    val isBusy: Boolean = false,
    val saveButtonLabel: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DexcomSettingsScreen(
    state: DexcomSettingsUiState,
    serverOptions: List<String>,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onServerChange: (String) -> Unit,
    onSave: () -> Unit,
    onDisconnect: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var serverExpanded by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    SecondaryScreenScaffold(
        title = stringResource(R.string.dexcom_settings_title),
        subtitle = stringResource(R.string.dexcom_settings_subtitle),
        onBack = onBack,
        modifier = modifier,
    ) {
        CompanionGroupedCard(modifier = Modifier.padding(top = 24.dp)) {
            CompanionListItem(
                icon = R.drawable.ic_sensor_glucose,
                title = state.accountSummary,
                subtitle = state.statusMessage,
            )
        }

        CompanionGroupedCard(modifier = Modifier.padding(top = 16.dp)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                OutlinedTextField(
                    value = state.username,
                    onValueChange = onUsernameChange,
                    label = { Text(stringResource(R.string.dexcom_username_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !state.isBusy,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.dexcom_password_hint)) },
                    singleLine = true,
                    visualTransformation =
                        if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                text =
                                    if (passwordVisible) {
                                        stringResource(R.string.dexcom_password_hide)
                                    } else {
                                        stringResource(R.string.dexcom_password_show)
                                    },
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    },
                    enabled = !state.isBusy,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                )
                Text(
                    text = stringResource(R.string.dexcom_region_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp),
                )
                ExposedDropdownMenuBox(
                    expanded = serverExpanded,
                    onExpandedChange = { if (!state.isBusy) serverExpanded = it },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                ) {
                    OutlinedTextField(
                        value = state.serverLabel,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !state.isBusy,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serverExpanded) },
                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = serverExpanded,
                        onDismissRequest = { serverExpanded = false },
                    ) {
                        serverOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onServerChange(option)
                                    serverExpanded = false
                                },
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.dexcom_encrypted_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 14.dp),
                )
                Button(
                    onClick = onSave,
                    enabled = !state.isBusy,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                ) {
                    Text(state.saveButtonLabel)
                }
            }
        }

        CompanionGroupedCard(modifier = Modifier.padding(top = 16.dp)) {
            CompanionListItem(
                icon = R.drawable.ic_lock_24,
                title = stringResource(R.string.dexcom_entry_disconnect),
                subtitle = stringResource(R.string.dexcom_entry_disconnect_message),
                onClick = if (!state.isBusy) onDisconnect else null,
            )
        }
    }
}
