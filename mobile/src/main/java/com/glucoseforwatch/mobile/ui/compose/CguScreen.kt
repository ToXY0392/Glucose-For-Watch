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
    ) {
        LegalBodyParagraph(
            text =
                "En utilisant l'application Glucose For Watch, vous acceptez les présentes " +
                    "Conditions Générales d'Utilisation.",
        )
        LegalBodyParagraph(
            text =
                "L'application est fournie « en l'état », sans aucune garantie expresse ou implicite " +
                    "de fonctionnement continu. Glucose For Watch dépend des serveurs de Dexcom, de la " +
                    "connectivité Bluetooth et du système d'exploitation de votre montre et de votre " +
                    "téléphone. Par conséquent, le développeur ne peut être tenu responsable des retards, " +
                    "des échecs de synchronisation ou de l'indisponibilité des données.",
        )
        LegalBodyParagraph(
            text =
                "Vous reconnaissez que cette application est un outil de confort destiné à faciliter " +
                    "l'affichage de vos données, et que vous l'utilisez à vos propres risques.",
        )
    }
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
