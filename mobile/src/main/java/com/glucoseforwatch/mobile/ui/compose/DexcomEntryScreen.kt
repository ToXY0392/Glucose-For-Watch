package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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

    SecondaryScreenScaffold(
        title = stringResource(R.string.dexcom_entry_title),
        subtitle = stringResource(R.string.dexcom_entry_subtitle),
        onBack = onBack,
        modifier = modifier,
    ) {
        CompanionGroupedCard(
            modifier = Modifier.padding(top = 24.dp),
        ) {
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
            )
            CompanionCardDivider()
            DexcomConsentListItem(
                checked = state.medicalWarningAccepted,
                label = stringResource(R.string.dexcom_entry_medical_checkbox),
                onCheckedChange = onMedicalWarningChange,
            )
        }

        Button(
            onClick = onPrimaryAction,
            enabled = primaryEnabled,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
        ) {
            Text(primaryLabel)
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

@Composable
private fun DexcomConsentListItem(
    checked: Boolean,
    label: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Checkbox) {
                    onCheckedChange(!checked)
                },
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
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
