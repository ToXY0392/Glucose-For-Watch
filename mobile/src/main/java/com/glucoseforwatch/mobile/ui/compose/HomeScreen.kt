package com.glucoseforwatch.mobile.ui.compose

import androidx.annotation.ColorInt
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.ui.HomeUiState

private val HomeGlucoseValueFontSize = 80.sp
private val HomeGlucoseUnitFontSize = 24.sp
private val HomeHeaderBottomSpacing = 48.dp
private val HomeRefreshSettingsSpacing = 40.dp
private val HomeSettingsBottomPadding = 32.dp
private val HomeDashboardDialSize = 320.dp
private val HomeDashboardDialBorderWidth = 3.dp
private val HomeDashboardDialItemSpacing = 16.dp

@Composable
fun HomeScreen(
    state: HomeUiState?,
    syncEnabled: Boolean,
    onSyncClick: () -> Unit,
    onDexcomClick: () -> Unit,
    onWatchClick: () -> Unit,
    onUnitClick: () -> Unit,
    onBatteryClick: () -> Unit,
    onPermissionsClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state == null) {
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            verticalArrangement = Arrangement.Top,
        ) {
            GlobalAppHeader(modifier = Modifier.fillMaxWidth())
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = HomeHeaderBottomSpacing),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        return
    }

    val uiState = state

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        GlobalAppHeader(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(HomeHeaderBottomSpacing))

        HomeDashboardDial(
            glucoseValue = uiState.heroGlucoseValue,
            glucoseValueColor = uiState.watchFaceValueColor,
            unitLabel = uiState.unitRowStatus,
            connectionLabel = uiState.connectionLabel,
            batteryLabel = uiState.batteryLabel,
            showBattery = uiState.showBattery,
            syncEnabled = syncEnabled,
            syncButtonTintColorRes = uiState.syncButtonTintColorRes,
            onSyncClick = onSyncClick,
        )

        Spacer(modifier = Modifier.height(HomeRefreshSettingsSpacing))

        Text(
            text = stringResource(R.string.home_companion_section_settings),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colorResource(R.color.wg7_companion_section),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, bottom = 12.dp),
        )

        HomeSettingsList(
            dexcomSubtitle = uiState.dexcomRowStatus,
            watchSubtitle = uiState.watchRowStatus,
            unitSubtitle = uiState.unitRowStatus,
            permissionsSubtitle = stringResource(R.string.home_companion_setting_permissions_sub),
            batterySubtitle = uiState.batterySettingSubtitle,
            onDexcomClick = onDexcomClick,
            onWatchClick = onWatchClick,
            onUnitClick = onUnitClick,
            onPermissionsClick = onPermissionsClick,
            onBatteryClick = onBatteryClick,
            onAboutClick = onAboutClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = HomeSettingsBottomPadding),
        )
    }
}

@Composable
private fun HomeDashboardDial(
    glucoseValue: String,
    @ColorInt glucoseValueColor: Int,
    unitLabel: String,
    connectionLabel: String,
    batteryLabel: String,
    showBattery: Boolean,
    syncEnabled: Boolean,
    @ColorInt syncButtonTintColorRes: Int,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(HomeDashboardDialSize)
                .border(
                    width = HomeDashboardDialBorderWidth,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(
                    HomeDashboardDialItemSpacing,
                    Alignment.CenterVertically,
                ),
        ) {
            HomeGlucoseValue(
                glucoseValue = glucoseValue,
                glucoseValueColor = glucoseValueColor,
                unitLabel = unitLabel,
            )

            HomeConnectionStatus(
                connectionLabel = connectionLabel,
                batteryLabel = batteryLabel,
                showBattery = showBattery,
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
}

@Composable
private fun HomeGlucoseValue(
    glucoseValue: String,
    @ColorInt glucoseValueColor: Int,
    unitLabel: String,
    modifier: Modifier = Modifier,
) {
    val statusColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = glucoseValue,
            color = Color(glucoseValueColor),
            fontSize = HomeGlucoseValueFontSize,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.02).sp,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.alignByBaseline(),
        )
        Text(
            text = unitLabel,
            color = statusColor,
            fontSize = HomeGlucoseUnitFontSize,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            modifier =
                Modifier
                    .alignByBaseline()
                    .padding(start = 8.dp),
        )
    }
}

@Composable
private fun HomeConnectionStatus(
    connectionLabel: String,
    batteryLabel: String,
    showBattery: Boolean,
    modifier: Modifier = Modifier,
) {
    val statusColor = MaterialTheme.colorScheme.onSurfaceVariant
    val statusStyle = MaterialTheme.typography.bodyMedium

    Row(
        modifier = modifier,
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

@Composable
private fun HomeStatusBullet(color: Color) {
    Text(
        text = "•",
        style = MaterialTheme.typography.bodyMedium,
        color = color.copy(alpha = 0.45f),
    )
}
