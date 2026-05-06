package com.widgetg7.mobile.sync

import android.content.Context
import com.widgetg7.core.model.GlucoseReading
import com.widgetg7.feature.sync.WearSyncPublisher
import com.widgetg7.mobile.watch.WatchConnectionRepository

class PhoneWearSyncService(private val context: Context) {
    /**
     * Envoie la donnée vers la montre sélectionnée via Data Layer.
     * @return false si aucune montre Wear n’est joignable (pas d’exception : la sync téléphone peut continuer).
     */
    private val publisher = WearSyncPublisher(context) {
        WatchConnectionRepository(context).loadStatus()
            .takeIf { it.connected && it.nodeId.isNotBlank() }
            ?.nodeId
    }

    suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long): Boolean =
        publisher.pushLatest(reading, sequenceId)
}
