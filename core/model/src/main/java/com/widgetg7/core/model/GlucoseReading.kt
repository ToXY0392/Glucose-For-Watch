package com.widgetg7.core.model

data class GlucoseReading(
    val valueMgDl: Int,
    val trend: String,
    val deltaMgDl: Int,
    val timestampEpochMs: Long,
    val stale: Boolean,
)
