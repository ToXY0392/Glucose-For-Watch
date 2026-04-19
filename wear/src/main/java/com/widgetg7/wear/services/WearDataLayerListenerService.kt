package com.widgetg7.wear.services

import android.content.ComponentName
import android.util.Log
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.widgetg7.wear.complication.GlucoseComplicationService
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.data.GlucoseKeys
import com.widgetg7.wear.data.GlucoseSnapshot
import com.widgetg7.wear.tile.GlucoseTileService

class WearDataLayerListenerService : WearableListenerService() {
    private val logTag = "WidgetG7Wear"

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        val cache = GlucoseCache(this)

        for (event in dataEvents) {
            if (event.type != DataEvent.TYPE_CHANGED) continue
            val item = event.dataItem
            if (item.uri.path != GlucoseKeys.PATH_LATEST) continue

            val map = DataMapItem.fromDataItem(item).dataMap
            val snapshot = GlucoseSnapshot(
                valueMgDl = map.getInt(GlucoseKeys.VALUE_MG_DL),
                trend = map.getString(GlucoseKeys.TREND).orEmpty(),
                deltaMgDl = map.getInt(GlucoseKeys.DELTA_MG_DL),
                timestampEpochMs = map.getLong(GlucoseKeys.TIMESTAMP_EPOCH_MS),
                stale = map.getBoolean(GlucoseKeys.STALE),
            )
            Log.d(
                logTag,
                "Received phone data value=${snapshot.valueMgDl} trend=${snapshot.trend} delta=${snapshot.deltaMgDl} stale=${snapshot.stale}",
            )
            cache.save(snapshot)

            ComplicationDataSourceUpdateRequester
                .create(this, ComponentName(this, GlucoseComplicationService::class.java))
                .requestUpdateAll()
            Log.d(logTag, "Requested complication refresh")

            TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)
            Log.d(logTag, "Requested tile refresh")
        }

        super.onDataChanged(dataEvents)
    }
}
