package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

private const val LEGAL_PREVIEW_WIDTH_DP = 411
private const val LEGAL_PREVIEW_HEIGHT_DP = 891
private const val LEGAL_PREVIEW_BACKGROUND = 0xFFF7F9F7

@Preview(
    name = "NoticeScreen",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = LEGAL_PREVIEW_WIDTH_DP,
    heightDp = LEGAL_PREVIEW_HEIGHT_DP,
    backgroundColor = LEGAL_PREVIEW_BACKGROUND,
)
@ShowkaseComposable
@Composable
internal fun NoticeScreenPreview() {
    LegalScreenPreviewHost {
        NoticeScreen(
            onBack = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "CguScreen",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = LEGAL_PREVIEW_WIDTH_DP,
    heightDp = LEGAL_PREVIEW_HEIGHT_DP,
    backgroundColor = LEGAL_PREVIEW_BACKGROUND,
)
@ShowkaseComposable
@Composable
internal fun CguScreenShowkasePreview() {
    LegalScreenPreviewHost {
        CguScreen(
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "PrivacyPolicyScreen",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = LEGAL_PREVIEW_WIDTH_DP,
    heightDp = LEGAL_PREVIEW_HEIGHT_DP,
    backgroundColor = LEGAL_PREVIEW_BACKGROUND,
)
@ShowkaseComposable
@Composable
internal fun PrivacyPolicyScreenShowkasePreview() {
    LegalScreenPreviewHost {
        PrivacyPolicyScreen(
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "MedicalWarningScreen",
    device = Devices.PIXEL_7,
    showBackground = true,
    widthDp = LEGAL_PREVIEW_WIDTH_DP,
    heightDp = LEGAL_PREVIEW_HEIGHT_DP,
    backgroundColor = LEGAL_PREVIEW_BACKGROUND,
)
@ShowkaseComposable
@Composable
internal fun MedicalWarningScreenShowkasePreview() {
    LegalScreenPreviewHost {
        MedicalWarningScreen(
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun LegalScreenPreviewHost(content: @Composable () -> Unit) {
    GlucoseForWatchTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            content()
        }
    }
}
