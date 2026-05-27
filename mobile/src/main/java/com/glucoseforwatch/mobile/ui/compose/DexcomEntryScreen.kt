package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.glucoseforwatch.mobile.R

data class DexcomEntryUiState(
    val legalTermsAccepted: Boolean = false,
    val medicalWarningAccepted: Boolean = false,
    val isConfigured: Boolean = false,
    val showDisconnectDialog: Boolean = false,
)

@Composable
fun DexcomEntryScreen(
    state: DexcomEntryUiState,
    onLegalTermsChange: (Boolean) -> Unit,
    onMedicalWarningChange: (Boolean) -> Unit,
    onPrimaryAction: () -> Unit,
    onConfirmDisconnect: () -> Unit,
    onDismissDisconnect: () -> Unit,
    onDocumentClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryEnabled =
        if (state.isConfigured) {
            true
        } else {
            state.legalTermsAccepted && state.medicalWarningAccepted
        }
    val primaryLabel =
        if (state.isConfigured) {
            stringResource(R.string.dexcom_entry_disconnect)
        } else {
            stringResource(R.string.dexcom_entry_connect)
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BrandHeader()
        Text(
            text = stringResource(R.string.dexcom_entry_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 14.dp),
        )
        Text(
            text = stringResource(R.string.dexcom_entry_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp),
        )
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                DexcomLegalLinksText(onDocumentClick = onDocumentClick)
                CheckboxRow(
                    checked = state.legalTermsAccepted,
                    onCheckedChange = onLegalTermsChange,
                    label = stringResource(R.string.dexcom_entry_legal_terms_checkbox),
                    modifier = Modifier.padding(top = 18.dp),
                )
                CheckboxRow(
                    checked = state.medicalWarningAccepted,
                    onCheckedChange = onMedicalWarningChange,
                    label = stringResource(R.string.dexcom_entry_medical_checkbox),
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text = stringResource(R.string.dexcom_entry_credentials_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 14.dp),
                )
                Button(
                    onClick = onPrimaryAction,
                    enabled = primaryEnabled,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 26.dp),
                ) {
                    Text(primaryLabel)
                }
            }
        }
        RoundBackButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp),
        )
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

@Composable
private fun CheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier =
                Modifier
                    .padding(start = 4.dp, top = 12.dp)
                    .weight(1f),
        )
    }
}
