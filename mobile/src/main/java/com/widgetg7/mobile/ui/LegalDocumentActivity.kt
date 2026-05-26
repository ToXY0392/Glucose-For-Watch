package com.widgetg7.mobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.widgetg7.mobile.ui.compose.LegalDocumentScreen
import com.widgetg7.mobile.ui.theme.WidgetG7Theme

/** Displays CGU, privacy policy, or medical disclaimer from raw assets. */
class LegalDocumentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val documentType = intent.getStringExtra(EXTRA_DOCUMENT_TYPE).orEmpty()
        val config = LegalDocuments.config(documentType)

        enableEdgeToEdge()
        setContent {
            WidgetG7Theme {
                LegalDocumentScreen(
                    config = config,
                    onBack = { finish() },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                )
            }
        }
    }

    companion object {
        const val EXTRA_DOCUMENT_TYPE = LegalDocuments.EXTRA_DOCUMENT_TYPE

        const val DOCUMENT_TYPE_CGU = LegalDocuments.DOCUMENT_TYPE_CGU
        const val DOCUMENT_TYPE_PRIVACY = LegalDocuments.DOCUMENT_TYPE_PRIVACY
        const val DOCUMENT_TYPE_MEDICAL = LegalDocuments.DOCUMENT_TYPE_MEDICAL
    }
}
