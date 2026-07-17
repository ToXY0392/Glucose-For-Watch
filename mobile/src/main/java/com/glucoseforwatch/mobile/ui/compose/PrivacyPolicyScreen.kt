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
    ) {
        LegalBodyParagraph(
            text = "La protection de vos données de santé est notre priorité absolue.",
        )
        LegalSectionHeading(text = "Fonctionnement local")
        LegalBodyParagraph(
            text =
                "Glucose For Watch agit uniquement comme une passerelle entre les serveurs Dexcom " +
                    "et votre montre connectée.",
        )
        LegalSectionHeading(text = "Identifiants et données")
        LegalBodyParagraph(
            text =
                "Vos identifiants de connexion Dexcom sont chiffrés et stockés localement et uniquement " +
                    "sur votre appareil. Les données glycémiques récupérées ne sont ni stockées de manière " +
                    "permanente, ni partagées avec des tiers, ni revendues, ni envoyées sur des serveurs " +
                    "externes nous appartenant. Elles transitent uniquement pour être affichées sur votre écran.",
        )
        LegalBodyParagraph(
            text =
                "En utilisant cette application, vous consentez à ce traitement local de vos informations.",
        )
    }
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
