package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

private const val PrivacyPolicyScreenTitle = "Politique de confidentialité"

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LegalScrollScreenLayout(
        title = PrivacyPolicyScreenTitle,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "Privacy Policy Screen")
@Composable
internal fun PrivacyPolicyScreenPreview() {
    GlucoseForWatchTheme {
        PrivacyPolicyScreen(
            onBackClick = {},
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        )
    }
}
