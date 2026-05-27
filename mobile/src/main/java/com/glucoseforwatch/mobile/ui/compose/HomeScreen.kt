package com.glucoseforwatch.mobile.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.ui.HomeUiState

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
    val uiState = state ?: return

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
    ) {
        Row(
            modifier =
                Modifier
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
                    tint = colorResource(uiState.syncButtonTintColorRes),
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .padding(top = 28.dp)
                    .size(260.dp)
                    .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.bg_watch_face_preview),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp),
            ) {
                Text(
                    text = uiState.watchFaceValueText,
                    color = Color(uiState.watchFaceValueColor),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.04).sp,
                )
                if (uiState.watchFaceMetaVisible) {
                    Text(
                        text = uiState.watchFaceMetaText,
                        color = colorResource(R.color.wg7_watch_face_meta),
                        fontSize = 14.sp,
                        letterSpacing = 0.02.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bluetooth_24),
                contentDescription = null,
                tint = colorResource(R.color.wg7_icon_tint),
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = uiState.connectionLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 6.dp),
            )
            Text(
                text = "·",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            if (uiState.showBattery) {
                Icon(
                    painter = painterResource(R.drawable.ic_battery_24),
                    contentDescription = null,
                    tint = colorResource(R.color.wg7_icon_tint),
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = uiState.batteryLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 6.dp),
                )
                Text(
                    text = "·",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
            Text(
                text = uiState.syncAgeLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (uiState.syncStatusLineVisible) {
            Text(
                text = uiState.syncStatusLine,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                color = colorResource(uiState.syncStatusLineTextColorRes),
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp)
                        .background(
                            color = colorResource(uiState.syncStatusLineBackgroundColorRes),
                            shape = RoundedCornerShape(28.dp),
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }

        Text(
            text = stringResource(R.string.home_companion_section_settings),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colorResource(R.color.wg7_companion_section),
            modifier =
                Modifier
                    .padding(start = 22.dp, top = 28.dp),
        )

        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 12.dp),
            shape = RoundedCornerShape(28.dp),
            color = colorResource(R.color.wg7_companion_group),
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                HomeSettingRow(
                    icon = R.drawable.ic_battery_saver_24,
                    title = stringResource(R.string.home_companion_setting_battery_title),
                    subtitle = uiState.batterySettingSubtitle,
                    onClick = onBatteryClick,
                )
                HomeSettingRow(
                    icon = R.drawable.ic_sensor_glucose,
                    title = stringResource(R.string.home_companion_setting_dexcom_title),
                    subtitle = uiState.dexcomRowStatus,
                    onClick = onDexcomClick,
                )
                HomeSettingRow(
                    icon = R.drawable.ic_settings_24,
                    title = stringResource(R.string.home_companion_setting_unit_title),
                    subtitle = uiState.unitRowStatus,
                    onClick = onUnitClick,
                )
                HomeSettingRow(
                    icon = R.drawable.ic_watch_24,
                    title = stringResource(R.string.home_companion_setting_watch_title),
                    subtitle = uiState.watchRowStatus,
                    onClick = onWatchClick,
                )
                if (uiState.showInstallRow) {
                    HomeSettingRow(
                        icon = R.drawable.ic_watch_install,
                        title = stringResource(R.string.home_companion_setting_install_title),
                        subtitle = stringResource(R.string.home_companion_setting_install_sub),
                        onClick = onInstallClick,
                    )
                }
                HomeSettingRow(
                    icon = R.drawable.ic_share_24,
                    title = stringResource(R.string.home_companion_setting_notice_title),
                    subtitle = stringResource(R.string.home_companion_setting_notice_sub),
                    onClick = onNoticeClick,
                )
                HomeSettingRow(
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
private fun HomeSettingRow(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 14.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron_forward_24),
            contentDescription = null,
            tint = colorResource(R.color.wg7_icon_tint),
            modifier = Modifier.size(20.dp),
        )
    }
}
