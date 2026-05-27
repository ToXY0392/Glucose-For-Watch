package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.ui.LegalDocuments
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DexcomLegalLinksText(
    onDocumentClick: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        maxItemsInEachRow = Int.MAX_VALUE,
    ) {
        Text(
            text = stringResource(R.string.dexcom_entry_legal_links_prefix),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LegalLink(stringResource(R.string.dexcom_entry_link_cgu)) {
            onDocumentClick(LegalDocuments.DOCUMENT_TYPE_CGU)
        }
        Text(
            text = stringResource(R.string.dexcom_entry_legal_links_mid_privacy),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LegalLink(stringResource(R.string.dexcom_entry_link_privacy)) {
            onDocumentClick(LegalDocuments.DOCUMENT_TYPE_PRIVACY)
        }
        Text(
            text = stringResource(R.string.dexcom_entry_legal_links_mid_medical),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LegalLink(stringResource(R.string.dexcom_entry_link_medical)) {
            onDocumentClick(LegalDocuments.DOCUMENT_TYPE_MEDICAL)
        }
        Text(
            text = ".",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LegalLink(
    label: String,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(
            text = label,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
