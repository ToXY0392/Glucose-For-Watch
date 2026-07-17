package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

private const val AboutScreenTitle = "Informations sur l'application"
private const val AboutScreenDescription =
    "Glucose For Watch a été conçue dans le but d'optimiser le suivi glycémique continu. " +
        "Notre engagement est de fournir une interface rigoureuse, réactive et fiable, " +
        "permettant la synchronisation en temps réel des données issues des capteurs Dexcom " +
        "et leur consultation immédiate sur montre connectée."
private const val AboutScreenSignature = "Développé avec Gemini sur Android Studio"
private const val AboutScreenVersionLabel = "Version 1.0.0"

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    versionLabel: String = AboutScreenVersionLabel,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp,
            ) {
                GlobalAppHeader(modifier = Modifier.fillMaxWidth())
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = AboutScreenTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = AboutScreenDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Justify,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                )
                Spacer(modifier = Modifier.height(120.dp))
                Text(
                    text = AboutScreenSignature,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = versionLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                )
            }
            RoundBackButton(
                onClick = onBackClick,
                modifier =
                    Modifier.padding(
                        top = 16.dp,
                        bottom = 24.dp,
                    ),
            )
        }
    }
}

@Preview(showBackground = true, name = "About Screen")
@Composable
internal fun AboutScreenPreview() {
    GlucoseForWatchTheme {
        AboutScreen(onBackClick = {})
    }
}
