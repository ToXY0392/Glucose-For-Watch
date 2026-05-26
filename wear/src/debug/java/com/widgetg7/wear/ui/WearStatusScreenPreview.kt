package com.widgetg7.wear.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.ScreenScaffold
import com.widgetg7.core.model.AgpGlucoseColors
import com.widgetg7.wear.ui.theme.WidgetG7WearTheme

private const val WEAR_SMALL_ROUND = "id:wearos_small_round"

/** Android Studio: open this file → Split / Design → pick a preview. */
private fun previewModel(
    value: String,
    valueColor: Int,
    trend: String = "↗",
    stale: Boolean = false,
    syncLine: String = "À jour",
    syncError: Boolean = false,
    battery: String? = "Batterie 72%",
): WearStatusUiModel =
    WearStatusUiModel(
        valueText = value,
        unitText = "mg/dL",
        trendArrow = trend,
        showTrend = trend.isNotEmpty(),
        valueColorArgb = valueColor,
        trendColorArgb = if (stale) AgpGlucoseColors.UNKNOWN else valueColor,
        stale = stale,
        syncStatusText = syncLine,
        syncStatusIsError = syncError,
        batteryLine = battery,
        healthMessage = null,
    )

@Preview(
    name = "In range 120",
    device = WEAR_SMALL_ROUND,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun WearStatusPreviewInRange() {
    WidgetG7WearTheme {
        ScreenScaffold {
            WearStatusScreenBody(
                model = previewModel("120", AgpGlucoseColors.IN_RANGE),
                complicationLinked = true,
                onSyncClick = {},
            )
        }
    }
}

@Preview(
    name = "High 200",
    device = WEAR_SMALL_ROUND,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun WearStatusPreviewHigh() {
    WidgetG7WearTheme {
        ScreenScaffold {
            WearStatusScreenBody(
                model = previewModel("200", AgpGlucoseColors.HIGH, trend = "→"),
                complicationLinked = true,
                onSyncClick = {},
            )
        }
    }
}

@Preview(
    name = "Stale",
    device = WEAR_SMALL_ROUND,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun WearStatusPreviewStale() {
    WidgetG7WearTheme {
        ScreenScaffold {
            WearStatusScreenBody(
                model =
                    previewModel(
                        "118",
                        AgpGlucoseColors.IN_RANGE,
                        stale = true,
                        syncLine = "Donnée périmée",
                        syncError = true,
                    ),
                complicationLinked = true,
                onSyncClick = {},
            )
        }
    }
}

@Preview(
    name = "No data",
    device = WEAR_SMALL_ROUND,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun WearStatusPreviewEmpty() {
    WidgetG7WearTheme {
        ScreenScaffold {
            WearStatusScreenBody(
                model =
                    previewModel(
                        "--",
                        AgpGlucoseColors.UNKNOWN,
                        trend = "",
                        syncLine = "Configurez Dexcom Share sur le téléphone.",
                        battery = null,
                    ),
                complicationLinked = true,
                onSyncClick = {},
            )
        }
    }
}
