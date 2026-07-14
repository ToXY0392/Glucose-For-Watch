package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

private const val MedicalWarningScreenTitle = "Avertissement médical"

@Composable
fun MedicalWarningScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LegalScrollScreenLayout(
        title = MedicalWarningScreenTitle,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "Medical Warning Screen")
@Composable
internal fun MedicalWarningScreenPreview() {
    GlucoseForWatchTheme {
        MedicalWarningScreen(
            onBackClick = {},
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        )
    }
}
