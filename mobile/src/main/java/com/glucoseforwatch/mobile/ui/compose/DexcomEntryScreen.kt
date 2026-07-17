package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.glucoseforwatch.mobile.R

data class DexcomEntryUiState(
    val isLoggedIn: Boolean = false,
    val username: String = "",
    val password: String = "",
    val serverLabel: String = "",
    val connectedUsername: String = "",
    val legalTermsAccepted: Boolean = false,
    val medicalWarningAccepted: Boolean = false,
    val isBusy: Boolean = false,
    val statusMessage: String = "",
    val showDisconnectDialog: Boolean = false,
    val connectButtonLabel: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DexcomEntryScreen(
    state: DexcomEntryUiState,
    serverOptions: List<String>,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onServerChange: (String) -> Unit,
    onLegalTermsChange: (Boolean) -> Unit,
    onMedicalWarningChange: (Boolean) -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onConfirmDisconnect: () -> Unit,
    onDismissDisconnect: () -> Unit,
    onDocumentClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var serverExpanded by remember { mutableStateOf(false) }
    var credentialVisible by remember { mutableStateOf(false) }

    val connectEnabled =
        !state.isLoggedIn &&
            state.legalTermsAccepted &&
            state.medicalWarningAccepted &&
            !state.isBusy

    val screenTitle =
        if (state.isLoggedIn) {
            stringResource(R.string.dexcom_entry_title_account)
        } else {
            stringResource(R.string.dexcom_entry_title_connect)
        }
    val screenSubtitle =
        if (state.isLoggedIn) {
            null
        } else {
            stringResource(R.string.dexcom_entry_subtitle)
        }

    CompanionAppScaffold(modifier = modifier) { paddingValues ->
        SecondaryScreenBody(
            title = screenTitle,
            subtitle = screenSubtitle,
            onBack = onBack,
            paddingValues = paddingValues,
        ) {
            Column(
                modifier = Modifier.padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (!state.isLoggedIn) {
                    DexcomCredentialsSection(
                        username = state.username,
                        credentialValue = state.password,
                        serverLabel = state.serverLabel,
                        serverOptions = serverOptions,
                        serverExpanded = serverExpanded,
                        credentialVisible = credentialVisible,
                        enabled = !state.isBusy,
                        onUsernameChange = onUsernameChange,
                        onPasswordChange = onPasswordChange,
                        onServerExpandedChange = { serverExpanded = it },
                        onCredentialVisibleChange = { credentialVisible = it },
                        onServerChange = onServerChange,
                    )

                    CompanionGroupedCard {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            DexcomLegalLinksText(onDocumentClick = onDocumentClick)
                            Text(
                                text = stringResource(R.string.dexcom_entry_credentials_notice),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp),
                            )
                        }
                        CompanionCardDivider()
                        DexcomConsentListItem(
                            checked = state.legalTermsAccepted,
                            label = stringResource(R.string.dexcom_entry_legal_terms_checkbox),
                            onCheckedChange = onLegalTermsChange,
                            enabled = !state.isBusy,
                        )
                        CompanionCardDivider()
                        DexcomConsentListItem(
                            checked = state.medicalWarningAccepted,
                            label = stringResource(R.string.dexcom_entry_medical_checkbox),
                            onCheckedChange = onMedicalWarningChange,
                            enabled = !state.isBusy,
                        )
                    }

                    if (state.statusMessage.isNotBlank()) {
                        Text(
                            text = state.statusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Button(
                        onClick = onConnect,
                        enabled = connectEnabled,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text =
                                state.connectButtonLabel.ifBlank {
                                    stringResource(R.string.dexcom_entry_connect)
                                },
                        )
                    }
                } else {
                    Text(
                        text =
                            stringResource(
                                R.string.dexcom_entry_connected_account,
                                state.connectedUsername,
                            ),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Button(
                        onClick = onDisconnect,
                        enabled = !state.isBusy,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.dexcom_entry_disconnect))
                    }
                }
            }
        }
    }

    if (state.showDisconnectDialog) {
        AlertDialog(
            onDismissRequest = onDismissDisconnect,
            title = { Text(stringResource(R.string.home_dexcom_disconnect_title)) },
            text = { Text(stringResource(R.string.dexcom_entry_disconnect_message)) },
            confirmButton = {
                TextButton(onClick = onConfirmDisconnect) {
                    Text(stringResource(R.string.dexcom_entry_disconnect))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDisconnect) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DexcomCredentialsSection(
    username: String,
    credentialValue: String,
    serverLabel: String,
    serverOptions: List<String>,
    serverExpanded: Boolean,
    credentialVisible: Boolean,
    enabled: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onServerExpandedChange: (Boolean) -> Unit,
    onCredentialVisibleChange: (Boolean) -> Unit,
    onServerChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text(stringResource(R.string.dexcom_username_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = credentialValue,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.dexcom_password_hint)) },
            singleLine = true,
            visualTransformation =
                if (credentialVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            trailingIcon = {
                TextButton(onClick = { onCredentialVisibleChange(!credentialVisible) }) {
                    Text(
                        text =
                            if (credentialVisible) {
                                stringResource(R.string.dexcom_password_hide)
                            } else {
                                stringResource(R.string.dexcom_password_show)
                            },
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.dexcom_region_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ExposedDropdownMenuBox(
            expanded = serverExpanded,
            onExpandedChange = { if (enabled) onServerExpandedChange(it) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = serverLabel,
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serverExpanded) },
                modifier =
                    Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = serverExpanded,
                onDismissRequest = { onServerExpandedChange(false) },
            ) {
                serverOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onServerChange(option)
                            onServerExpandedChange(false)
                        },
                    )
                }
            }
        }
        Text(
            text = stringResource(R.string.dexcom_encrypted_notice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DexcomConsentListItem(
    checked: Boolean,
    label: String,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,
                    role = Role.Checkbox,
                    onClick = { onCheckedChange(!checked) },
                ),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.padding(start = 8.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 4.dp, top = 14.dp, end = 16.dp, bottom = 14.dp),
        )
    }
}
