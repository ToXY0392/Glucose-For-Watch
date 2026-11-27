package com.glucoseforwatch.mobile.ui.compose

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.glucoseforwatch.mobile.ui.theme.GlassPalette
import kotlinx.coroutines.delay

/** Shared easing of the home motion: fast start, soft landing. */
internal val GlassEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

/**
 * Motion is off in Studio previews, in snapshot exports ([enabled] = false) and when the
 * user removed animations (Accessibility or Developer options: animator scale 0).
 */
@Composable
internal fun rememberHomeMotion(enabled: Boolean): Boolean {
    val inspection = LocalInspectionMode.current
    val context = LocalContext.current
    return remember(enabled, inspection, context) {
        val animatorScale =
            runCatching {
                Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
            }.getOrDefault(1f)
        enabled && !inspection && animatorScale > 0f
    }
}

/** Entrance: fades in and rises 16 dp after [delayMillis]; static when [motion] is off. */
internal fun Modifier.riseIn(delayMillis: Int, motion: Boolean): Modifier =
    composed {
        val progress = remember { Animatable(if (motion) 0f else 1f) }
        LaunchedEffect(motion) {
            if (motion) {
                delay(delayMillis.toLong())
                progress.animateTo(1f, tween(700, easing = GlassEasing))
            } else {
                progress.snapTo(1f)
            }
        }
        val rise = with(LocalDensity.current) { 16.dp.toPx() }
        graphicsLayer {
            alpha = progress.value
            translationY = (1f - progress.value) * rise
        }
    }

/**
 * Translucent glass card. Pressing it scales it to 96 % and lays a very light white veil
 * over it; a card without [onClick] still gives that feedback but is not a button.
 */
@Composable
internal fun GlassSurface(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    motion: Boolean = true,
    shape: Shape = RoundedCornerShape(24.dp),
    fill: Color = GlassPalette.GlassFill,
    stroke: Color = GlassPalette.GlassStroke,
    content: @Composable BoxScope.() -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val clickPressed by source.collectIsPressedAsState()
    var touchPressed by remember { mutableStateOf(false) }
    val pressed = clickPressed || touchPressed
    val scale by animateFloatAsState(
        targetValue = if (pressed && motion) 0.96f else 1f,
        animationSpec = tween(160),
        label = "glassScale",
    )
    val veil by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(if (pressed) 80 else 350),
        label = "glassVeil",
    )
    val interaction =
        if (onClick != null) {
            Modifier.clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
        } else {
            Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        touchPressed = true
                        tryAwaitRelease()
                        touchPressed = false
                    },
                )
            }
        }
    Box(
        modifier =
            modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(shape)
                .background(fill)
                .border(1.dp, stroke, shape)
                .then(interaction)
                .drawWithContent {
                    drawContent()
                    if (veil > 0f) drawRect(GlassPalette.PressOverlay, alpha = veil)
                },
        content = content,
    )
}

/**
 * Two-color Menthe & Citron background: a diagonal mint-to-lemon wash and two soft halos.
 * Halos are radial gradients (no RenderEffect), so they render the same on every API level.
 */
@Composable
internal fun GlassBackground(motion: Boolean, modifier: Modifier = Modifier) {
    val (breathe, drift) = if (motion) animatedHalos() else 1f to 0f
    val density = LocalDensity.current
    val driftX = with(density) { 40.dp.toPx() }
    val driftY = with(density) { (-36).dp.toPx() }
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush =
                Brush.linearGradient(
                    0f to GlassPalette.BackgroundStart,
                    0.55f to GlassPalette.BackgroundMiddle,
                    1f to GlassPalette.BackgroundEnd,
                    start = Offset.Zero,
                    end = Offset(size.width * 0.55f, size.height),
                ),
        )
        drawHalo(
            color = GlassPalette.HaloMint,
            alpha = 0.42f,
            center = Offset(size.width * 0.03f, size.height * 0.07f),
            radius = size.width * 0.95f * breathe,
        )
        drawHalo(
            color = GlassPalette.HaloLemon,
            alpha = 0.62f,
            center = Offset(size.width * 0.85f + driftX * drift, size.height * 0.9f + driftY * drift),
            radius = size.width * 1.15f,
        )
    }
}

@Composable
private fun animatedHalos(): Pair<Float, Float> {
    val transition = rememberInfiniteTransition(label = "glassHalos")
    val breathe by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(4500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "mintBreathe",
    )
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "lemonDrift",
    )
    return breathe to drift
}

/** Soft blurred-looking disc: the color fades to transparent towards [radius]. */
internal fun DrawScope.drawHalo(color: Color, alpha: Float, center: Offset, radius: Float) {
    drawCircle(
        brush =
            Brush.radialGradient(
                colors = listOf(color.copy(alpha = alpha), color.copy(alpha = alpha * 0.45f), color.copy(alpha = 0f)),
                center = center,
                radius = radius,
            ),
        radius = radius,
        center = center,
    )
}
