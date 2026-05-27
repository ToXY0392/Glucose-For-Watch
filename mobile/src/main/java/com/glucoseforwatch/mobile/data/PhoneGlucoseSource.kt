package com.glucoseforwatch.mobile.data

import com.glucoseforwatch.core.model.GlucoseReading

/** Abstraction for fetching the latest glucose reading on the phone. */
interface PhoneGlucoseSource {
    val sourceName: String
    suspend fun latest(): GlucoseReading
}
