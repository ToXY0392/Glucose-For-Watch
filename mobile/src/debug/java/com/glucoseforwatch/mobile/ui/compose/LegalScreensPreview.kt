package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.ui.LegalDocumentConfig
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

@Preview(name = "NoticeScreen", showBackground = true)
@ShowkaseComposable
@Composable
internal fun NoticeScreenPreview() {
    GlucoseForWatchTheme {
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
@ShowkaseComposable
@Composable
internal fun LegalDocumentScreenPreview() {
    GlucoseForWatchTheme {
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
