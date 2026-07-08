package com.glucoseforwatch.mobile.ui.compose

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.ui.HomeUiState

private val HomeSettingsCardShape = RoundedCornerShape(28.dp)

@Composable
fun HomeScreen(
    state: HomeUiState?,
    syncEnabled: Boolean,
    onSyncClick: () -> Unit,
    onDexcomClick: () -> Unit,
    onWatchClick: () -> Unit,
    onUnitClick: () -> Unit,
    onBatteryClick: () -> Unit,
    onInstallClick: () -> Unit,
    onNoticeClick: () -> Unit,
    onPermissionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state == null) {
        Box(
            modifier = modifier.fillMaxSize().statusBarsPadding(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }
    val uiState = state

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        HomeHeader(
            syncEnabled = syncEnabled,
            syncButtonTintColorRes = uiState.syncButtonTintColorRes,
            onSyncClick = onSyncClick,
        )

        HomeGlucoseHero(
            glucoseValue = uiState.heroGlucoseValue,
            glucoseValueColor = uiState.watchFaceValueColor,
            unitLabel = uiState.unitRowStatus,
            connectionLabel = uiState.connectionLabel,
            batteryLabel = uiState.batteryLabel,
            showBattery = uiState.showBattery,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.home_companion_section_settings),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colorResource(R.color.wg7_companion_section),
            modifier = Modifier.padding(start = 22.dp),
        )

        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 12.dp),
            shape = HomeSettingsCardShape,
            colors =
                CardDefaults.cardColors(
                    containerColor = colorResource(R.color.wg7_companion_group),
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column {
                HomeSettingsListItem(
                    icon = R.drawable.ic_battery_saver_24,
                    title = stringResource(R.string.home_companion_setting_battery_title),
                    subtitle = uiState.batterySettingSubtitle,
                    onClick = onBatteryClick,
                )
                HomeSettingsDivider()
                HomeSettingsListItem(
                    icon = R.drawable.ic_sensor_glucose,
                    title = stringResource(R.string.home_companion_setting_dexcom_title),
                    subtitle = uiState.dexcomRowStatus,
                    onClick = onDexcomClick,
                )
                HomeSettingsDivider()
                HomeSettingsListItem(
                    icon = R.drawable.ic_settings_24,
                    title = stringResource(R.string.home_companion_setting_unit_title),
                    subtitle = uiState.unitRowStatus,
                    onClick = onUnitClick,
                )
                HomeSettingsDivider()
                HomeSettingsListItem(
                    icon = R.drawable.ic_watch_24,
                    title = stringResource(R.string.home_companion_setting_watch_title),
                    subtitle = uiState.watchRowStatus,
                    onClick = onWatchClick,
                )
                if (uiState.showInstallRow) {
                    HomeSettingsDivider()
                    HomeSettingsListItem(
                        icon = R.drawable.ic_watch_install,
                        title = stringResource(R.string.home_companion_setting_install_title),
                        subtitle = stringResource(R.string.home_companion_setting_install_sub),
                        onClick = onInstallClick,
                    )
                }
                HomeSettingsDivider()
                HomeSettingsListItem(
                    icon = R.drawable.ic_share_24,
                    title = stringResource(R.string.home_companion_setting_notice_title),
                    subtitle = stringResource(R.string.home_companion_setting_notice_sub),
                    onClick = onNoticeClick,
                )
                HomeSettingsDivider()
                HomeSettingsListItem(
                    icon = R.drawable.ic_lock_24,
                    title = stringResource(R.string.home_companion_setting_permissions_title),
                    subtitle = stringResource(R.string.home_companion_setting_permissions_sub),
                    onClick = onPermissionsClick,
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    syncEnabled: Boolean,
    syncButtonTintColorRes: Int,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 8.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        IconButton(
            onClick = onSyncClick,
            enabled = syncEnabled,
            modifier = Modifier.alpha(if (syncEnabled) 1f else 0.45f),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_refresh_24),
                contentDescription = stringResource(R.string.home_sync_content_description),
                tint = colorResource(syncButtonTintColorRes),
            )
        }
    }
}

@Composable
private fun HomeGlucoseHero(
    glucoseValue: String,
    @ColorInt glucoseValueColor: Int,
    unitLabel: String,
    connectionLabel: String,
    batteryLabel: String,
    showBattery: Boolean,
    modifier: Modifier = Modifier,
) {
    val statusColor = MaterialTheme.colorScheme.onSurfaceVariant
    val statusStyle = MaterialTheme.typography.bodyMedium

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = glucoseValue,
                color = Color(glucoseValueColor),
                fontSize = 72.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.02).sp,
            )
            Text(
                text = unitLabel,
                color = statusColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 6.dp, bottom = 10.dp),
            )
        }

        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bluetooth_24),
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = connectionLabel,
                style = statusStyle,
                color = statusColor,
            )
            if (showBattery) {
                HomeStatusBullet(color = statusColor)
                Icon(
                    painter = painterResource(R.drawable.ic_battery_24),
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = batteryLabel,
                    style = statusStyle,
                    color = statusColor,
                )
            }
        }
    }
}

@Composable
private fun HomeStatusBullet(color: Color) {
    Text(
        text = "•",
        style = MaterialTheme.typography.bodyMedium,
        color = color.copy(alpha = 0.45f),
    )
}

@Composable
private fun HomeSettingsDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

@Composable
private fun HomeSettingsListItem(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
        colors =
            ListItemDefaults.colors(
                containerColor = colorResource(R.color.wg7_companion_group),
            ),
        leadingContent = {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(
                            color = colorResource(R.color.wg7_icon_tonal_fill),
                            shape = RoundedCornerShape(12.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = colorResource(R.color.wg7_icon_tint),
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_forward_24),
                contentDescription = null,
                tint = colorResource(R.color.wg7_icon_tint),
                modifier = Modifier.size(20.dp),
            )
        },
    )
}
