package com.widgetg7.feature.sync

sealed interface SyncExecutionResult {
    data class SuccessNewReading(val sourceName: String) : SyncExecutionResult
    data class SuccessNoNewReading(val sourceName: String) : SyncExecutionResult
    data class Failure(val message: String) : SyncExecutionResult
}
