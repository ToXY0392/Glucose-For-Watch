package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.ui.LegalDocuments

@Composable
fun DexcomLegalLinksText(
    onDocumentClick: (String) -> Unit,
) {
    val cguLabel = stringResource(R.string.dexcom_entry_link_cgu)
    val privacyLabel = stringResource(R.string.dexcom_entry_link_privacy)
    val medicalLabel = stringResource(R.string.dexcom_entry_link_medical)

    val bodyStyle =
        MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    val plainStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)
    val linkStyle =
        SpanStyle(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

    val annotatedText =
        buildAnnotatedString {
            fun plain(text: String) {
                withStyle(plainStyle) {
                    append(text)
                }
            }

            fun link(
                label: String,
                documentType: String,
            ) {
                withLink(
                    LinkAnnotation.Clickable(tag = documentType) {
                        onDocumentClick(documentType)
                    },
                ) {
                    withStyle(linkStyle) {
                        append(label)
                    }
                }
            }

            plain("Lire les ")
            link(cguLabel, LegalDocuments.DOCUMENT_TYPE_CGU)
            plain(", la ")
            link(privacyLabel, LegalDocuments.DOCUMENT_TYPE_PRIVACY)
            plain(" et l'")
            link(medicalLabel, LegalDocuments.DOCUMENT_TYPE_MEDICAL)
            plain(".")
        }

    Text(
        text = annotatedText,
        style = bodyStyle,
    )
}
