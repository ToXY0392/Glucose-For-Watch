package com.widgetg7.mobile.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.widgetg7.mobile.R
import com.widgetg7.mobile.ui.LegalDocumentConfig
import com.widgetg7.mobile.ui.theme.WidgetG7Theme

@Preview(name = "NoticeScreen", showBackground = true)
@Composable
private fun NoticeScreenPreview() {
    WidgetG7Theme {
        NoticeScreen(
            onBack = {},
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        )
    }
}

@Preview(name = "LegalDocumentScreen — CGU", showBackground = true)
@Composable
private fun LegalDocumentScreenPreview() {
    WidgetG7Theme {
        LegalDocumentScreen(
            config =
                LegalDocumentConfig(
                    titleRes = R.string.legal_title_cgu,
                    rawResId = R.raw.cgu,
                ),
            onBack = {},
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        )
    }
}
