package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
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
    SecondaryScreenScaffold(
        title = stringResource(R.string.glucose_unit_settings_title),
        subtitle = stringResource(R.string.glucose_unit_settings_subtitle),
        onBack = onBack,
        modifier = modifier,
    ) {
        CompanionGroupedCard(modifier = Modifier.padding(top = 24.dp)) {
            GlucoseUnitListItem(
                label = stringResource(R.string.glucose_unit_mg_dl),
                selected = selectedUnit == GlucoseDisplayUnit.MG_DL,
                onClick = { onUnitSelected(GlucoseDisplayUnit.MG_DL) },
            )
            CompanionCardDivider()
            GlucoseUnitListItem(
                label = stringResource(R.string.glucose_unit_mmol_l),
                selected = selectedUnit == GlucoseDisplayUnit.MMOL_L,
                onClick = { onUnitSelected(GlucoseDisplayUnit.MMOL_L) },
            )
        }
    }
}

@Composable
private fun GlucoseUnitListItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.RadioButton, onClick = onClick),
        colors =
            ListItemDefaults.colors(
                containerColor = colorResource(R.color.wg7_companion_group),
            ),
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = null,
            )
        },
    )
}
