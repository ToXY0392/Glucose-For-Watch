package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.mobile.R

@Composable
fun GlucoseUnitSettingsScreen(
    selectedUnit: GlucoseDisplayUnit,
    onUnitSelected: (GlucoseDisplayUnit) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BrandHeader()
        Text(
            text = stringResource(R.string.glucose_unit_settings_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
        )
        Text(
            text = stringResource(R.string.glucose_unit_settings_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
        )
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                UnitOptionRow(
                    label = stringResource(R.string.glucose_unit_mg_dl),
                    selected = selectedUnit == GlucoseDisplayUnit.MG_DL,
                    onClick = { onUnitSelected(GlucoseDisplayUnit.MG_DL) },
                )
                UnitOptionRow(
                    label = stringResource(R.string.glucose_unit_mmol_l),
                    selected = selectedUnit == GlucoseDisplayUnit.MMOL_L,
                    onClick = { onUnitSelected(GlucoseDisplayUnit.MMOL_L) },
                )
            }
        }
        RoundBackButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 22.dp, bottom = 28.dp),
        )
    }
}

@Composable
private fun UnitOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.RadioButton, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
