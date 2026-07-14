package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.ui.HomeUiState

private val HomeGlucoseValueFontSize = 80.sp
private val HomeGlucoseUnitFontSize = 24.sp
private val HomeGlucoseValueLineHeight = 88.dp
private val HomeHeaderBottomSpacing = 48.dp
private val HomeRefreshSettingsSpacing = 40.dp
private val HomeSettingsBottomPadding = 32.dp
private val HomeDashboardDialSize = 320.dp
private val HomeDashboardDialBorderWidth = 3.dp
private val HomeDashboardDialItemSpacing = 16.dp
private val HomeDialColorAnimationSpec = tween<Color>(durationMillis = 500)
private val HomeDialValueSlideAnimationSpec = tween<IntOffset>(durationMillis = 500)

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
            glucoseValueMgDl = uiState.heroGlucoseValueMgDl,
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
    glucoseValueMgDl: Int?,
    unitLabel: String,
    connectionLabel: String,
    batteryLabel: String,
    showBattery: Boolean,
    syncEnabled: Boolean,
    syncButtonTintColorRes: Int,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val glucoseBand = HomeDialGlucoseColor.band(glucoseValueMgDl)
    val fallbackColor = MaterialTheme.colorScheme.onSurfaceVariant
    val targetDialColor =
        if (glucoseBand is HomeDialGlucoseBand.Unknown) fallbackColor else glucoseBand.color
    val animatedDialColor by animateColorAsState(
        targetValue = targetDialColor,
        animationSpec = HomeDialColorAnimationSpec,
        label = "homeDialColor",
    )

    Box(
        modifier =
            modifier
                .size(HomeDashboardDialSize)
                .border(
                    width = HomeDashboardDialBorderWidth,
                    color = animatedDialColor,
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
                glucoseValueMgDl = glucoseValueMgDl,
                unitLabel = unitLabel,
                animatedValueColor = animatedDialColor,
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
    glucoseValueMgDl: Int?,
    unitLabel: String,
    animatedValueColor: Color,
    modifier: Modifier = Modifier,
) {
    val statusColor = MaterialTheme.colorScheme.onSurfaceVariant
    val displayState = HomeDialGlucoseDisplay(text = glucoseValue, valueMgDl = glucoseValueMgDl)

    Row(
        modifier = modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .height(HomeGlucoseValueLineHeight)
                    .wrapContentWidth(),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = displayState,
                transitionSpec = {
                    val oldMg = initialState.valueMgDl
                    val newMg = targetState.valueMgDl
                    val increasing = oldMg != null && newMg != null && newMg > oldMg
                    if (increasing) {
                        slideInVertically(
                            animationSpec = HomeDialValueSlideAnimationSpec,
                            initialOffsetY = { fullHeight -> fullHeight },
                        ) togetherWith
                            slideOutVertically(
                                animationSpec = HomeDialValueSlideAnimationSpec,
                                targetOffsetY = { fullHeight -> -fullHeight },
                            )
                    } else {
                        slideInVertically(
                            animationSpec = HomeDialValueSlideAnimationSpec,
                            initialOffsetY = { fullHeight -> -fullHeight },
                        ) togetherWith
                            slideOutVertically(
                                animationSpec = HomeDialValueSlideAnimationSpec,
                                targetOffsetY = { fullHeight -> fullHeight },
                            )
                    }
                },
                label = "homeGlucoseValue",
            ) { display ->
                Text(
                    text = display.text,
                    color = animatedValueColor,
                    fontSize = HomeGlucoseValueFontSize,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.02).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    softWrap = false,
                )
            }
        }
        Text(
            text = unitLabel,
            color = statusColor,
            fontSize = HomeGlucoseUnitFontSize,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            softWrap = false,
            modifier = Modifier.padding(start = 8.dp),
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
