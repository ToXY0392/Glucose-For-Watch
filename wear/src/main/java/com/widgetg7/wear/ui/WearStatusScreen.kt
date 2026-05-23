package com.widgetg7.wear.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.widgetg7.wear.R

/**
 * Wear status screen: AGP glucose hero, sync state, battery — mirrors tile semantics.
 *
 * Chrome uses ToXY mint; glucose value color is AGP-only ([WearStatusUiModel.valueColorArgb]).
 */
@Composable
fun WearStatusScreen(
    refreshKey: Int,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val model =
        remember(refreshKey) {
            WearStatusUiModelFactory.load(context)
        }

    ScreenScaffold(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = model.valueText,
                color = Color(model.valueColorArgb),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = model.unitText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (model.showTrend) {
                    Text(
                        text = " ${model.trendArrow}",
                        color = Color(model.trendColorArgb),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.syncStatusText,
                style = MaterialTheme.typography.labelMedium,
                color =
                    when {
                        model.syncStatusIsError -> MaterialTheme.colorScheme.error
                        model.stale -> Color(0xFFFB923C)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            model.healthMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }

            model.batteryLine?.let { battery ->
                Text(
                    text = battery,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSyncClick,
                modifier = Modifier.fillMaxWidth(0.85f),
            ) {
                Text(text = stringResource(R.string.wear_status_sync_now))
            }
        }
    }
}
