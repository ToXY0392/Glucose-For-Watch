package com.glucoseforwatch.core.model

/** Single glucose measurement from a remote source, ready for sync and display. */
data class GlucoseReading(
    val valueMgDl: Int,
    val trend: String,
    val deltaMgDl: Int,
    val timestampEpochMs: Long,
    val stale: Boolean,
)
