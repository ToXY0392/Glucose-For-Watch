package com.widgetg7.mobile.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.widgetg7.mobile.R

/** Displays CGU, privacy policy, or medical disclaimer from raw assets. */
class LegalDocumentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_document)

        val documentType = intent.getStringExtra(EXTRA_DOCUMENT_TYPE).orEmpty()
        val config = documentConfig(documentType)

        findViewById<TextView>(R.id.legalDocumentTitleText).text = config.title
        findViewById<TextView>(R.id.legalDocumentContentText).text =
            resources.openRawResource(config.rawResId)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }
    }

    private fun documentConfig(type: String): LegalDocumentConfig {
        return when (type) {
            DOCUMENT_TYPE_PRIVACY ->
                LegalDocumentConfig(
                    title = "Politique de confidentialité",
                    rawResId = R.raw.politique_confidentialite,
                )

            DOCUMENT_TYPE_MEDICAL ->
                LegalDocumentConfig(
                    title = "Avertissement médical",
                    rawResId = R.raw.avertissement_medical,
                )

            else ->
                LegalDocumentConfig(
                    title = "CGU",
                    rawResId = R.raw.cgu,
                )
        }
    }

    /** Title and raw asset for a legal document type. */
    data class LegalDocumentConfig(
        val title: String,
        val rawResId: Int,
    )

    companion object {
        const val EXTRA_DOCUMENT_TYPE = "extra_document_type"

        const val DOCUMENT_TYPE_CGU = "cgu"
        const val DOCUMENT_TYPE_PRIVACY = "privacy"
        const val DOCUMENT_TYPE_MEDICAL = "medical"
    }
}
