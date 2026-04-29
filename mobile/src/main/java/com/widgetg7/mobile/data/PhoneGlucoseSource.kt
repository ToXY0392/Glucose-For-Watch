package com.widgetg7.mobile.data

data class GlucoseReading(
    val valueMgDl: Int,
    val trend: String,
    val deltaMgDl: Int,
    val timestampEpochMs: Long,
    val stale: Boolean,
)

interface PhoneGlucoseSource {
    val sourceName: String
    suspend fun latest(): GlucoseReading
}
