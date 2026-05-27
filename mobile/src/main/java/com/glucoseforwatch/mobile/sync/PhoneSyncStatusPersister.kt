package com.glucoseforwatch.mobile.sync

import com.glucoseforwatch.core.model.GlucoseReading
import com.glucoseforwatch.feature.sync.SyncExecutionResult
import com.glucoseforwatch.feature.sync.SyncStatusRepository
import com.glucoseforwatch.feature.sync.WatchDeliveryStatus

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
