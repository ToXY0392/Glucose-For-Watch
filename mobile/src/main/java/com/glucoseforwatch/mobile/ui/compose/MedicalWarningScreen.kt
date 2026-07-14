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
    ) {
        LegalSectionHeading(text = "AVERTISSEMENT IMPORTANT")
        LegalBodyParagraph(
            text =
                "Glucose For Watch N'EST PAS un dispositif médical. Cette application n'a pas été " +
                    "évaluée ni approuvée par la FDA, l'EMA ou toute autre autorité de santé publique.",
        )
        LegalBodyParagraph(
            text =
                "Elle ne remplace en aucun cas l'application officielle Dexcom, le récepteur Dexcom " +
                    "fourni par le fabricant, ni l'avis d'un professionnel de santé. Les données affichées " +
                    "peuvent subir des décalages temporels ou des erreurs de transmission.",
        )
        LegalBodyParagraph(
            text =
                "Ne prenez jamais de décision thérapeutique (comme l'administration d'insuline ou la prise " +
                    "de sucre) en vous basant uniquement sur les valeurs affichées sur cette application. " +
                    "Fiez-vous toujours à votre application Dexcom officielle ou à un lecteur de glycémie " +
                    "capillaire en cas de doute, de symptômes discordants ou pour toute décision de traitement.",
        )
    }
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
