package com.glucoseforwatch.core.datalayer.contract

import kotlinx.coroutines.flow.Flow

interface GlucoseDataSource {
    fun getGlucoseReadings(): Flow<GlucoseReading>
}