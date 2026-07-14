package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.glucoseforwatch.mobile.R

data class WearInstallerUiState(
    val ip: String = "",
    val pairPort: String = "",
    val pairCode: String = "",
    val adbPort: String = "",
    val busy: Boolean = false,
    val installEnabled: Boolean = true,
    val showEmbeddedApkMissing: Boolean = false,
    val statusMessage: String? = null,
    val statusIsError: Boolean = false,
    val scrollToDirectSectionToken: Int = 0,
)

@Composable
fun WearInstallerScreen(
    state: WearInstallerUiState,
    onIpChange: (String) -> Unit,
    onPairPortChange: (String) -> Unit,
    onPairCodeChange: (String) -> Unit,
    onAdbPortChange: (String) -> Unit,
    onOcrClick: () -> Unit,
    onPairClick: () -> Unit,
    onInstallClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val directSectionRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(state.scrollToDirectSectionToken) {
        if (state.scrollToDirectSectionToken > 0) {
            directSectionRequester.bringIntoView()
        }
    }

    CompanionAppScaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                        .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
        Text(
            text = stringResource(R.string.wear_install_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
        )
        Text(
            text = stringResource(R.string.wear_install_reassurance),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
        )
        Text(
            text = stringResource(R.string.wear_install_intro),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
        )

        WearInstallInfoCard(
            modifier = Modifier.padding(top = 16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Text(
                text = stringResource(R.string.wear_install_checklist_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.wear_install_checklist_1),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = stringResource(R.string.wear_install_checklist_2),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                text = stringResource(R.string.wear_install_checklist_3),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp),
            )
        }

        SectionHeading(
            text = stringResource(R.string.wear_install_section_watch),
            modifier = Modifier.padding(top = 24.dp),
        )
        WearInstallStepCard(
            label = stringResource(R.string.wear_install_step1_label),
            title = stringResource(R.string.wear_install_step1_title),
            body = stringResource(R.string.wear_install_step1_body),
            modifier = Modifier.padding(top = 12.dp),
        )
        WearInstallStepCard(
            label = stringResource(R.string.wear_install_step2_label),
            title = stringResource(R.string.wear_install_step2_title),
            body = stringResource(R.string.wear_install_step2_body),
            modifier = Modifier.padding(top = 12.dp),
        )

        SectionHeading(
            text = stringResource(R.string.wear_install_section_phone),
            modifier = Modifier.padding(top = 22.dp),
        )

        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .bringIntoViewRequester(directSectionRequester),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Text(
                    text = stringResource(R.string.wear_install_direct_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.wear_install_direct_lead),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Text(
                    text = stringResource(R.string.wear_install_direct_sub_1),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorResource(R.color.wg7_accent_dark),
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = stringResource(R.string.wear_install_direct_help_pair),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
                OutlinedButton(
                    onClick = onOcrClick,
                    enabled = !state.busy,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                ) {
                    Text(stringResource(R.string.wear_install_direct_ocr_btn))
                }
                OutlinedTextField(
                    value = state.ip,
                    onValueChange = onIpChange,
                    label = { Text(stringResource(R.string.wear_install_direct_hint_ip)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                )
                OutlinedTextField(
                    value = state.pairPort,
                    onValueChange = onPairPortChange,
                    label = { Text(stringResource(R.string.wear_install_direct_hint_pair_port)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                )
                OutlinedTextField(
                    value = state.pairCode,
                    onValueChange = { value -> onPairCodeChange(value.filter(Char::isDigit).take(6)) },
                    label = { Text(stringResource(R.string.wear_install_direct_hint_pair_code)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                )
                OutlinedButton(
                    onClick = onPairClick,
                    enabled = !state.busy,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                ) {
                    Text(stringResource(R.string.wear_install_direct_pair_btn))
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 18.dp),
                    color = MaterialTheme.colorScheme.outline,
                )

                Text(
                    text = stringResource(R.string.wear_install_direct_sub_2),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorResource(R.color.wg7_accent_dark),
                    modifier = Modifier.padding(top = 18.dp),
                )
                Text(
                    text = stringResource(R.string.wear_install_direct_help_install),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
                OutlinedTextField(
                    value = state.adbPort,
                    onValueChange = onAdbPortChange,
                    label = { Text(stringResource(R.string.wear_install_direct_hint_adb_port)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                )
                Button(
                    onClick = onInstallClick,
                    enabled = !state.busy && state.installEnabled,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                ) {
                    Text(stringResource(R.string.wear_install_direct_install_btn))
                }
                if (state.busy) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 12.dp),
                    )
                }
                state.statusMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color =
                            colorResource(
                                if (state.statusIsError) R.color.wg7_danger else R.color.wg7_accent_dark,
                            ),
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
                if (state.showEmbeddedApkMissing) {
                    Text(
                        text = stringResource(R.string.wear_install_apk_missing),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(R.color.wg7_danger),
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }
        }

        SectionHeading(
            text = stringResource(R.string.wear_install_section_finish),
            modifier = Modifier.padding(top = 20.dp),
        )
        WearInstallStepCard(
            label = stringResource(R.string.wear_install_finish_label),
            title = stringResource(R.string.wear_install_finish_title),
            body = stringResource(R.string.wear_install_finish_body),
            modifier = Modifier.padding(top = 10.dp),
        )
        Text(
            text = stringResource(R.string.wear_install_after),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
        )
        RoundBackButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
            }
        }
    }
}

@Composable
private fun SectionHeading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun WearInstallInfoCard(
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
private fun WearInstallStepCard(
    label: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    WearInstallInfoCard(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = colorResource(R.color.wg7_accent_dark),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}
