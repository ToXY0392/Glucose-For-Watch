package com.glucoseforwatch.mobile.sync

import android.content.Context
import com.google.android.gms.wearable.Wearable
import com.glucoseforwatch.core.model.GlucoseReading
import com.glucoseforwatch.feature.sync.WearSyncPublisher
import com.glucoseforwatch.mobile.settings.DisplaySettingsStore
import com.glucoseforwatch.mobile.watch.WatchConnectionRepository
import kotlinx.coroutines.tasks.await

/** Pushes the latest reading to the selected watch via the Wear Data Layer. */
class PhoneWearSyncService(private val context: Context) {
    private val publisher = WearSyncPublisher(
        context = context,
        resolveTargetNodeId = {
            WatchConnectionRepository(context).loadStatus()
                .takeIf { it.connected && it.nodeId.isNotBlank() }
                ?.nodeId
        },
        resolveSourceNodeId = {
            runCatching { Wearable.getNodeClient(context).localNode.await().id }.getOrNull()
        },
    )

    /**
     * @return false when no reachable Wear node exists (no exception; phone sync may continue).
     */
    suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long): Boolean {
        val displayUnit = DisplaySettingsStore(context).loadGlucoseDisplayUnit()
        return publisher.pushLatest(reading, sequenceId, displayUnit)
    }
}
