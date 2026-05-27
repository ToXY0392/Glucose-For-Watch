package com.glucoseforwatch.feature.sync

import com.glucoseforwatch.core.model.GlucoseReading

/** Persists the latest glucose reading when a watch push fails so it can be retried later. */
interface PendingPushPort {
    fun hasPending(): Boolean
    fun enqueue(reading: GlucoseReading)
    fun clear()
}
