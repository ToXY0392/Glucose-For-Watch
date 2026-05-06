package com.widgetg7.mobile.data

import com.widgetg7.core.model.GlucoseReading

interface PhoneGlucoseSource {
    val sourceName: String
    suspend fun latest(): GlucoseReading
}
