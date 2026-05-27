package com.glucoseforwatch.mobile.watch

import androidx.annotation.DrawableRes
import com.glucoseforwatch.mobile.R

/** Hero drawable and labels for the connected watch model. */
data class WatchVisual(
    @DrawableRes val drawableResId: Int,
    val headline: String,
    val supportLabel: String?,
)

/** Picks watch hero artwork from device name and health metadata. */
object WatchVisualResolver {
    fun resolve(displayName: String, health: WatchSyncHealthStatus?): WatchVisual {
        val manufacturer = health?.manufacturer.orEmpty()
        val model = health?.model.orEmpty()
        val device = health?.device.orEmpty()
        val signature = listOf(displayName, manufacturer, model, device)
            .joinToString(" ")
            .lowercase()

        return when {
            signature.contains("pixel watch 2") ->
                WatchVisual(
                    drawableResId = R.drawable.watch_reference_hero,
                    headline = "Google Pixel Watch 2",
                    supportLabel = "Montre Google connectée",
                )

            signature.contains("pixel watch 3") ->
                WatchVisual(
                    drawableResId = R.drawable.watch_reference_hero,
                    headline = "Google Pixel Watch 3",
                    supportLabel = "Montre Google connectée",
                )

            signature.contains("pixel watch") ->
                WatchVisual(
                    drawableResId = R.drawable.watch_reference_hero,
                    headline = model.ifBlank { displayName.ifBlank { "Google Pixel Watch" } },
                    supportLabel = "Montre Google connectée",
                )

            signature.contains("galaxy watch") ->
                WatchVisual(
                    drawableResId = R.drawable.illustration_round_watch_generic,
                    headline = model.ifBlank { displayName.ifBlank { "Galaxy Watch" } },
                    supportLabel = "Montre Samsung connectée",
                )

            displayName.isNotBlank() ->
                WatchVisual(
                    drawableResId = R.drawable.illustration_round_watch_generic,
                    headline = displayName,
                    supportLabel = manufacturer.ifBlank { "Montre connectée" },
                )

            else ->
                WatchVisual(
                    drawableResId = R.drawable.illustration_round_watch_generic,
                    headline = "Aucune montre détectée",
                    supportLabel = "Connectez une montre Wear OS",
                )
        }
    }
}
