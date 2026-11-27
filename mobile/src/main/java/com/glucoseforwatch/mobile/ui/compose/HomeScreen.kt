package com.glucoseforwatch.mobile.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.core.model.GlucoseRange
import com.glucoseforwatch.core.model.GlucoseUnitFormatter
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.ui.GlucoseDisplayFormatter
import com.glucoseforwatch.mobile.ui.HomeUiState
import com.glucoseforwatch.mobile.ui.theme.GlassPalette
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TileGap = 12.dp
private val TilePadding = 16.dp

/**
 * Home "Menthe & Citron": Bento of glass cards over a mint-to-lemon background, in motion.
 * Glucose numbers and range marks take their color from [HomeGlucoseColor] only.
 */
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
    syncJustSucceeded: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    motionEnabled: Boolean = true,
) {
    val motion = rememberHomeMotion(motionEnabled)
    Box(modifier = modifier.fillMaxSize()) {
        GlassBackground(motion = motion)
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(TileGap),
        ) {
            HomeHeader(ageLabel = state?.syncAgeLabel, modifier = Modifier.riseIn(0, motion))
            if (state == null) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GlassPalette.Accent)
                }
                return@Column
            }
            GlucoseCard(state = state, motion = motion, modifier = Modifier.riseIn(80, motion))
            Row(horizontalArrangement = Arrangement.spacedBy(TileGap)) {
                WatchTile(
                    state = state,
                    motion = motion,
                    onClick = onWatchClick,
                    modifier = Modifier.weight(1f).riseIn(160, motion),
                )
                SyncTile(
                    busy = !syncEnabled,
                    done = syncJustSucceeded,
                    motion = motion,
                    onClick = onSyncClick,
                    modifier = Modifier.weight(1f).riseIn(220, motion),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(TileGap)) {
                InfoTile(
                    icon = R.drawable.ic_sensor_glucose,
                    title = stringResource(R.string.home_companion_setting_dexcom_title),
                    subtitle = state.dexcomRowStatus,
                    motion = motion,
                    onClick = onDexcomClick,
                    modifier = Modifier.weight(1f).riseIn(280, motion),
                )
                UnitTile(
                    unitLabel = state.unitRowStatus,
                    motion = motion,
                    onClick = onUnitClick,
                    modifier = Modifier.weight(1f).riseIn(340, motion),
                )
            }
            SettingsCard(
                batterySubtitle = state.batterySettingSubtitle,
                motion = motion,
                onBatteryClick = onBatteryClick,
                onPermissionsClick = onPermissionsClick,
                onAboutClick = onAboutClick,
                modifier = Modifier.riseIn(400, motion),
            )
        }
    }
}

@Composable
private fun HomeHeader(ageLabel: String?, modifier: Modifier = Modifier) {
    val today =
        remember {
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH)).uppercase(Locale.FRENCH)
        }
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = today, color = GlassPalette.InkTertiary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Text(
                text = stringResource(R.string.app_name),
                color = GlassPalette.Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp,
            )
        }
        if (!ageLabel.isNullOrBlank()) {
            Text(
                text = ageLabel,
                color = GlassPalette.Ink,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x8CFFFFFF))
                        .border(1.dp, GlassPalette.GlassStroke, RoundedCornerShape(50))
                        .padding(horizontal = 11.dp, vertical = 7.dp),
            )
        }
    }
}

@Composable
private fun GlucoseCard(state: HomeUiState, motion: Boolean, modifier: Modifier = Modifier) {
    val valueMgDl = state.heroGlucoseValueMgDl
    val stale = state.heroStale
    val unit = state.heroDisplayUnit
    val agpColor by animateColorAsState(
        targetValue = HomeGlucoseColor.color(valueMgDl, stale),
        animationSpec = tween(if (motion) 600 else 0),
        label = "glucoseColor",
    )
    val shownMgDl by animateIntAsState(
        targetValue = valueMgDl ?: 0,
        animationSpec = tween(if (motion) 750 else 0, easing = GlassEasing),
        label = "glucoseCount",
    )
    val valueText = if (valueMgDl == null) state.heroGlucoseValue else GlucoseDisplayFormatter.formatValue(shownMgDl, unit)
    val bandLabel = bandLabel(valueMgDl, stale)
    val a11y =
        stringResource(
            R.string.home_glucose_a11y,
            state.heroGlucoseValue,
            state.unitRowStatus,
            bandLabel.orEmpty(),
            state.heroTrendLabel,
            state.syncAgeLabel,
        )

    val pop = remember { Animatable(1f) }
    val firstValue = remember { valueMgDl }
    LaunchedEffect(valueMgDl) {
        if (motion && valueMgDl != null && valueMgDl != firstValue) {
            pop.animateTo(1.06f, tween(200, easing = GlassEasing))
            pop.animateTo(1f, tween(300, easing = GlassEasing))
        }
    }

    GlassSurface(
        modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) {
            contentDescription = a11y
            liveRegion = LiveRegionMode.Polite
        },
        motion = motion,
        shape = RoundedCornerShape(28.dp),
    ) {
        BandGlow(color = agpColor, motion = motion, modifier = Modifier.align(Alignment.TopEnd))
        Column(
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.home_glucose_title),
                    color = GlassPalette.InkTertiary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                if (bandLabel != null) {
                    Text(
                        text = bandLabel,
                        color = GlassPalette.Ink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier =
                            Modifier
                                .border(1.5.dp, agpColor, RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = valueText,
                    color = agpColor,
                    fontSize = 88.sp,
                    lineHeight = 84.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-4).sp,
                    maxLines = 1,
                    softWrap = false,
                    modifier =
                        Modifier.graphicsLayer {
                            scaleX = pop.value
                            scaleY = pop.value
                            transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 1f)
                        },
                )
                Column(modifier = Modifier.padding(bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TrendArrow(trend = state.heroTrend, color = agpColor, motion = motion)
                    Text(text = state.unitRowStatus, color = GlassPalette.InkSecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    text = if (valueMgDl == null) state.watchFaceMetaText else state.heroTrendLabel,
                    color = GlassPalette.InkSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    modifier = Modifier.weight(1f).padding(bottom = 10.dp),
                )
            }
            AgpScale(valueMgDl = valueMgDl, stale = stale, unit = unit, motion = motion)
        }
    }
}

@Composable
private fun bandLabel(valueMgDl: Int?, stale: Boolean): String? {
    if (valueMgDl == null) return null
    if (stale) return stringResource(R.string.home_band_stale)
    return when (HomeGlucoseColor.range(valueMgDl, stale = false)) {
        GlucoseRange.VERY_LOW -> stringResource(R.string.home_band_very_low)
        GlucoseRange.LOW -> stringResource(R.string.home_band_low)
        GlucoseRange.IN_RANGE -> stringResource(R.string.home_band_in_range)
        GlucoseRange.HIGH -> stringResource(R.string.home_band_high)
        GlucoseRange.VERY_HIGH -> stringResource(R.string.home_band_very_high)
        GlucoseRange.UNKNOWN -> stringResource(R.string.home_band_stale)
    }
}

/** Soft disc of the reading's AGP color in the card corner; breathes while motion is on. */
@Composable
private fun BandGlow(color: Color, motion: Boolean, modifier: Modifier = Modifier) {
    val scale =
        if (motion) {
            val transition = rememberInfiniteTransition(label = "bandGlow")
            val value by transition.animateFloat(
                initialValue = 1f,
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(tween(4500), RepeatMode.Reverse),
                label = "bandGlowScale",
            )
            value
        } else {
            1f
        }
    Canvas(modifier = modifier.offset(x = 60.dp, y = (-70).dp).size(220.dp)) {
        drawHalo(color = color, alpha = 0.18f, center = center, radius = size.minDimension / 2f * scale)
    }
}

/** Dexcom trend arrow, rotated per token; NONE draws nothing (no false "stable" arrow). */
@Composable
private fun TrendArrow(trend: String, color: Color, motion: Boolean) {
    val degrees =
        when (trend) {
            "UP" -> -90f
            "UP_RIGHT" -> -45f
            "FLAT" -> 0f
            "DOWN_RIGHT" -> 45f
            "DOWN" -> 90f
            else -> null
        } ?: return
    val rotation by animateFloatAsState(degrees, tween(if (motion) 700 else 0, easing = GlassEasing), label = "trendRotation")
    val float =
        if (motion) {
            val transition = rememberInfiniteTransition(label = "trendFloat")
            val value by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1300), RepeatMode.Reverse), label = "trendFloatY")
            value
        } else {
            0f
        }
    Canvas(
        modifier =
            Modifier
                .size(26.dp)
                .graphicsLayer { translationY = -3.dp.toPx() * float }
                .rotate(rotation),
    ) {
        val s = size.width / 24f
        val path =
            Path().apply {
                moveTo(5 * s, 12 * s)
                lineTo(19 * s, 12 * s)
                moveTo(13 * s, 6 * s)
                lineTo(19 * s, 12 * s)
                lineTo(13 * s, 18 * s)
            }
        drawPath(path, color, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** 40–400 mg/dL bar in the standard AGP colors, with a marker on the current reading. */
@Composable
private fun AgpScale(valueMgDl: Int?, stale: Boolean, unit: GlucoseDisplayUnit, motion: Boolean) {
    val fraction by animateFloatAsState(
        targetValue = valueMgDl?.let { HomeGlucoseColor.scaleFraction(it) } ?: 0f,
        animationSpec = tween(if (motion) 900 else 0, easing = GlassEasing),
        label = "scaleMarker",
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(18.dp), contentAlignment = Alignment.CenterStart) {
            Row(modifier = Modifier.fillMaxWidth().height(8.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                HomeGlucoseColor.scaleSegments.forEach { segment ->
                    Box(
                        modifier =
                            Modifier
                                .weight(segment.spanMgDl)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(segment.color.copy(alpha = segment.alpha)),
                    )
                }
            }
            if (valueMgDl != null && !stale) {
                Box(
                    modifier =
                        Modifier
                            .offset { IntOffset((maxWidth * fraction - 3.dp).roundToPx(), 0) }
                            .width(6.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(GlassPalette.Ink)
                            .border(1.5.dp, Color.White, RoundedCornerShape(3.dp)),
                )
            }
        }
        // Each AGP threshold sits on its color boundary, to scale.
        Box(modifier = Modifier.fillMaxWidth()) {
            HomeGlucoseColor.scaleTicksMgDl.forEach { tick ->
                Text(
                    text = scaleTickLabel(tick, unit),
                    color = GlassPalette.InkTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(BiasAlignment(HomeGlucoseColor.scaleFraction(tick) * 2f - 1f, 0f)),
                )
            }
        }
    }
}

/** Plain tick numbers: the reading formatter turns 40 and 400 into the LOW / HI sentinels. */
private fun scaleTickLabel(tickMgDl: Int, unit: GlucoseDisplayUnit): String {
    val value = GlucoseUnitFormatter.toRangedDisplayValue(tickMgDl, unit)
    return when (unit) {
        GlucoseDisplayUnit.MG_DL -> tickMgDl.toString()
        GlucoseDisplayUnit.MMOL_L -> String.format(Locale.US, "%.1f", value)
    }
}

@Composable
private fun WatchTile(state: HomeUiState, motion: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GlassSurface(modifier = modifier, onClick = onClick, motion = motion) {
        Column(modifier = Modifier.padding(TilePadding), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TileIcon(R.drawable.ic_watch_24)
            Column {
                Text(stringResource(R.string.home_companion_setting_watch_title), color = GlassPalette.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LiveDot(active = state.watchConnected, motion = motion)
                    Text(state.connectionLabel, color = GlassPalette.InkSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            val percent = state.watchBatteryPercent
            if (percent != null) {
                BatteryBar(percent = percent, label = state.batteryLabel, motion = motion)
            }
        }
    }
}

/** Status dot; pulses a soft ring while the watch is connected. */
@Composable
private fun LiveDot(active: Boolean, motion: Boolean) {
    val color = if (active) GlassPalette.Accent else GlassPalette.InkTertiary
    val pulse =
        if (active && motion) {
            val transition = rememberInfiniteTransition(label = "liveDot")
            val value by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1800, easing = LinearEasing)), label = "liveDotPulse")
            value
        } else {
            null
        }
    Box(
        modifier =
            Modifier
                .size(7.dp)
                .drawBehind {
                    if (pulse != null) {
                        drawCircle(color.copy(alpha = 0.45f * (1f - pulse)), radius = size.minDimension / 2f + 8.dp.toPx() * pulse)
                    }
                    drawCircle(color)
                },
    )
}

@Composable
private fun BatteryBar(percent: Int, label: String, motion: Boolean) {
    val target = percent.coerceIn(0, 100) / 100f
    val fill = remember { Animatable(if (motion) 0f else target) }
    LaunchedEffect(target, motion) {
        if (motion) {
            if (fill.value == 0f) kotlinx.coroutines.delay(500)
            fill.animateTo(target, tween(1200, easing = GlassEasing))
        } else {
            fill.snapTo(target)
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(GlassPalette.Track),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(fill.value)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(GlassPalette.Accent),
            )
        }
        Text(label, color = GlassPalette.Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SyncTile(busy: Boolean, done: Boolean, motion: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val fill by animateColorAsState(
        targetValue = if (done) GlassPalette.AccentPressed.copy(alpha = 0.95f) else GlassPalette.Accent.copy(alpha = 0.9f),
        animationSpec = tween(400),
        label = "syncFill",
    )
    val spin =
        if (busy && motion) {
            val transition = rememberInfiniteTransition(label = "syncSpin")
            val value by transition.animateFloat(0f, 360f, infiniteRepeatable(tween(900, easing = LinearEasing)), label = "syncSpinAngle")
            value
        } else {
            0f
        }
    val title =
        when {
            busy -> stringResource(R.string.home_sync_tile_busy)
            done -> stringResource(R.string.home_sync_tile_done)
            else -> stringResource(R.string.home_sync_tile_title)
        }
    GlassSurface(
        modifier = modifier,
        onClick = onClick,
        enabled = !busy,
        motion = motion,
        fill = fill,
        stroke = Color(0x66FFFFFF),
    ) {
        Column(modifier = Modifier.padding(TilePadding), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(GlassPalette.Lemon),
                contentAlignment = Alignment.Center,
            ) {
                if (done && !busy) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val s = size.width / 24f
                        val path =
                            Path().apply {
                                moveTo(5 * s, 12.5f * s)
                                lineTo(9.5f * s, 17 * s)
                                lineTo(19 * s, 7.5f * s)
                            }
                        drawPath(path, GlassPalette.Accent, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_refresh_24),
                        contentDescription = null,
                        tint = GlassPalette.Accent,
                        modifier = Modifier.size(20.dp).rotate(spin),
                    )
                }
            }
            Column {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.home_sync_tile_sub), color = GlassPalette.OnAccentSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun InfoTile(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    motion: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassSurface(modifier = modifier, onClick = onClick, motion = motion) {
        Column(modifier = Modifier.padding(TilePadding), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TileIcon(icon)
            Column {
                Text(title, color = GlassPalette.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = GlassPalette.InkSecondary, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun UnitTile(unitLabel: String, motion: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GlassSurface(modifier = modifier, onClick = onClick, motion = motion) {
        Column(modifier = Modifier.padding(TilePadding), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(unitLabel, color = GlassPalette.Accent, fontSize = 22.sp, lineHeight = 22.sp, fontWeight = FontWeight.ExtraBold)
            Column {
                Text(stringResource(R.string.home_unit_tile_title), color = GlassPalette.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.home_unit_tile_hint), color = GlassPalette.InkSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun TileIcon(@DrawableRes icon: Int) {
    Icon(painter = painterResource(icon), contentDescription = null, tint = GlassPalette.Accent, modifier = Modifier.size(22.dp))
}

@Composable
private fun SettingsCard(
    batterySubtitle: String,
    motion: Boolean,
    onBatteryClick: () -> Unit,
    onPermissionsClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassSurface(modifier = modifier.fillMaxWidth(), motion = motion, fill = Color(0x7AFFFFFF)) {
        Column {
            SettingsRow(R.drawable.ic_battery_saver_24, stringResource(R.string.home_companion_setting_battery_title), batterySubtitle, motion, onBatteryClick)
            HorizontalDivider(thickness = 1.dp, color = GlassPalette.Divider)
            SettingsRow(
                R.drawable.ic_lock_24,
                stringResource(R.string.home_companion_setting_permissions_title),
                stringResource(R.string.home_companion_setting_permissions_sub),
                motion,
                onPermissionsClick,
            )
            HorizontalDivider(thickness = 1.dp, color = GlassPalette.Divider)
            SettingsRow(
                R.drawable.ic_brand_mark,
                stringResource(R.string.home_companion_setting_about_title),
                stringResource(R.string.home_companion_setting_about_sub),
                motion,
                onAboutClick,
            )
        }
    }
}

@Composable
private fun SettingsRow(@DrawableRes icon: Int, title: String, subtitle: String, motion: Boolean, onClick: () -> Unit) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        motion = motion,
        shape = RoundedCornerShape(0.dp),
        fill = Color.Transparent,
        stroke = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(painter = painterResource(icon), contentDescription = null, tint = GlassPalette.Accent, modifier = Modifier.size(18.dp))
            Text(title, color = GlassPalette.Ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = subtitle,
                color = GlassPalette.InkSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_forward_24),
                contentDescription = null,
                tint = GlassPalette.Chevron,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}
