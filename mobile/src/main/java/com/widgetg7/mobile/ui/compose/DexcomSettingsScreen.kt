package com.widgetg7.mobile.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.widgetg7.mobile.R

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

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BrandHeader()
        Text(
            text = stringResource(R.string.dexcom_settings_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
        )
        Text(
            text = stringResource(R.string.dexcom_settings_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
        )
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Text(
                    text = state.accountSummary,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = state.statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
                OutlinedTextField(
                    value = state.username,
                    onValueChange = onUsernameChange,
                    label = { Text(stringResource(R.string.dexcom_username_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !state.isBusy,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
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
                        TextButtonLikeToggle(
                            label =
                                if (passwordVisible) {
                                    stringResource(R.string.dexcom_password_hide)
                                } else {
                                    stringResource(R.string.dexcom_password_show)
                                },
                            onClick = { passwordVisible = !passwordVisible },
                        )
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
                OutlinedButton(
                    onClick = onDisconnect,
                    enabled = !state.isBusy,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                ) {
                    Text(stringResource(R.string.home_dexcom_disconnect))
                }
            }
        }
        RoundBackButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 14.dp),
        )
    }
}

@Composable
private fun TextButtonLikeToggle(
    label: String,
    onClick: () -> Unit,
) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
