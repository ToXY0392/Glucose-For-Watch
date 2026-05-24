package com.widgetg7.feature.sync

sealed interface SyncExecutionResult {
    val sourceName: String
    val watchDelivery: WatchDeliveryStatus

    data class SuccessNewReading(
        override val sourceName: String,
        override val watchDelivery: WatchDeliveryStatus,
    ) : SyncExecutionResult

    data class SuccessNoNewReading(
        override val sourceName: String,
        override val watchDelivery: WatchDeliveryStatus,
    ) : SyncExecutionResult

    data class Failure(val message: String) : SyncExecutionResult {
        override val sourceName: String = ""
        override val watchDelivery: WatchDeliveryStatus = WatchDeliveryStatus.NOT_APPLICABLE
    }
}
