package com.widgetg7.mobile.sync

import com.widgetg7.core.model.GlucoseReading
import com.widgetg7.feature.sync.SyncExecutionResult
import com.widgetg7.feature.sync.SyncStatusRepository
import com.widgetg7.feature.sync.WatchDeliveryStatus

internal object PhoneSyncStatusPersister {
    fun persist(
        repository: SyncStatusRepository,
        result: SyncExecutionResult,
        reading: GlucoseReading,
    ) {
        when (result) {
            is SyncExecutionResult.Failure -> Unit
            is SyncExecutionResult.SuccessNewReading,
            is SyncExecutionResult.SuccessNoNewReading,
            -> {
                when (result.watchDelivery) {
                    WatchDeliveryStatus.DELIVERED ->
                        repository.saveSuccess(result.sourceName, reading)
                    WatchDeliveryStatus.QUEUED,
                    WatchDeliveryStatus.WATCH_UNAVAILABLE,
                    -> repository.saveWatchDeliveryPending(result.sourceName, reading)
                    WatchDeliveryStatus.NOT_APPLICABLE ->
                        repository.saveFetchedReading(result.sourceName, reading)
                }
            }
        }
    }
}
