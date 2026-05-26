package com.widgetg7.feature.sync

import com.widgetg7.core.model.GlucoseReading

/** Persists the latest glucose reading when a watch push fails so it can be retried later. */
interface PendingPushPort {
    fun hasPending(): Boolean
    fun enqueue(reading: GlucoseReading)
    fun clear()
}
