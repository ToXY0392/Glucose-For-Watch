package com.widgetg7.mobile.data

import com.widgetg7.core.model.GlucoseReading

/** Abstraction for fetching the latest glucose reading on the phone. */
interface PhoneGlucoseSource {
    val sourceName: String
    suspend fun latest(): GlucoseReading
}
