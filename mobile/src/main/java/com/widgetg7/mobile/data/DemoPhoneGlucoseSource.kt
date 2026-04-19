package com.widgetg7.mobile.data

import kotlin.random.Random

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

class DemoPhoneGlucoseSource : PhoneGlucoseSource {
    private val trends = listOf("UP", "UP_RIGHT", "FLAT", "DOWN_RIGHT", "DOWN")
    override val sourceName: String = "demo"

    override suspend fun latest(): GlucoseReading {
        val value = Random.nextInt(75, 220)
        val delta = Random.nextInt(-15, 16)
        return GlucoseReading(
            valueMgDl = value,
            trend = trends.random(),
            deltaMgDl = delta,
            timestampEpochMs = System.currentTimeMillis(),
            stale = false,
        )
    }
}
