package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

private const val CguScreenTitle = "Conditions Générales d'Utilisation"

@Composable
fun CguScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LegalScrollScreenLayout(
        title = CguScreenTitle,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "CGU Screen")
@Composable
internal fun CguScreenPreview() {
    GlucoseForWatchTheme {
        CguScreen(
            onBackClick = {},
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        )
    }
}
