package com.glucoseforwatch.mobile.ui

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.glucoseforwatch.mobile.R

/** Legal document types and raw asset mapping for CGU, privacy, and medical disclaimer. */
object LegalDocuments {
    const val EXTRA_DOCUMENT_TYPE = "extra_document_type"

    const val DOCUMENT_TYPE_CGU = "cgu"
    const val DOCUMENT_TYPE_PRIVACY = "privacy"
    const val DOCUMENT_TYPE_MEDICAL = "medical"

    fun config(type: String): LegalDocumentConfig =
        when (type) {
            DOCUMENT_TYPE_PRIVACY ->
                LegalDocumentConfig(
                    titleRes = R.string.legal_title_privacy,
                    rawResId = R.raw.politique_confidentialite,
                )

            DOCUMENT_TYPE_MEDICAL ->
                LegalDocumentConfig(
                    titleRes = R.string.legal_title_medical,
                    rawResId = R.raw.avertissement_medical,
                )

            else ->
                LegalDocumentConfig(
                    titleRes = R.string.legal_title_cgu,
                    rawResId = R.raw.cgu,
                )
        }
}

data class LegalDocumentConfig(
    @StringRes val titleRes: Int,
    @RawRes val rawResId: Int,
)
