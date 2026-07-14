package com.glucoseforwatch.mobile.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.glucoseforwatch.mobile.R

private val SettingItemHorizontalPadding = 22.dp
private val SettingItemVerticalPadding = 14.dp
private val SettingIconSize = 20.dp
private val SettingChevronSize = 18.dp
private val SettingDividerThickness = 0.5.dp

@Composable
fun HomeSettingsList(
    dexcomSubtitle: String,
    watchSubtitle: String,
    unitSubtitle: String,
    permissionsSubtitle: String,
    batterySubtitle: String,
    onDexcomClick: () -> Unit,
    onWatchClick: () -> Unit,
    onUnitClick: () -> Unit,
    onPermissionsClick: () -> Unit,
    onBatteryClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // 1. Dexcom
        SettingItem(
            icon = R.drawable.ic_sensor_glucose,
            title = stringResource(R.string.home_companion_setting_dexcom_title),
            subtitle = dexcomSubtitle,
            onClick = onDexcomClick,
        )
        SettingDivider()

        // 2. Montre
        SettingItem(
            icon = R.drawable.ic_watch_24,
            title = stringResource(R.string.home_companion_setting_watch_title),
            subtitle = watchSubtitle,
            onClick = onWatchClick,
        )
        SettingDivider()

        // 3. Unité glycémique
        SettingItem(
            icon = R.drawable.ic_settings_24,
            title = stringResource(R.string.home_companion_setting_unit_title),
            subtitle = unitSubtitle,
            onClick = onUnitClick,
        )
        SettingDivider()

        // 4. Autorisations
        SettingItem(
            icon = R.drawable.ic_lock_24,
            title = stringResource(R.string.home_companion_setting_permissions_title),
            subtitle = permissionsSubtitle,
            onClick = onPermissionsClick,
        )
        SettingDivider()

        // 5. Sync en veille
        SettingItem(
            icon = R.drawable.ic_battery_saver_24,
            title = stringResource(R.string.home_companion_setting_battery_title),
            subtitle = batterySubtitle,
            onClick = onBatteryClick,
        )
        SettingDivider()

        // 6. À propos
        SettingItem(
            icon = R.drawable.ic_brand_mark,
            title = stringResource(R.string.home_companion_setting_about_title),
            subtitle = stringResource(R.string.home_companion_setting_about_sub),
            onClick = onAboutClick,
        )
    }
}

@Composable
fun SettingItem(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showChevron: Boolean = true,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick)
                .padding(
                    horizontal = SettingItemHorizontalPadding,
                    vertical = SettingItemVerticalPadding,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = colorResource(R.color.wg7_icon_tint),
            modifier = Modifier.size(SettingIconSize),
        )
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * 0.85f,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 0.85f,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (showChevron) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_forward_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.size(SettingChevronSize),
            )
        }
    }
}

@Composable
fun SettingDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier =
            modifier.padding(
                start = SettingItemHorizontalPadding + SettingIconSize + 12.dp,
            ),
        thickness = SettingDividerThickness,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    )
}
