package com.glucoseforwatch.core.datalayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the wire values shared by phone and watch. An installed watch keeps the old values,
 * so changing one breaks sync until both apps are updated: change these tests on purpose only.
 */
class GlucoseDataLayerContractTest {
    @Test
    fun sync_paths_are_stable() {
        assertEquals("/glucose/latest", GlucoseDataLayerContract.PATH_LATEST)
        assertEquals("/glucose/refresh/request", GlucoseDataLayerContract.PATH_REFRESH_REQUEST)
        assertEquals("/glucose/refresh/status", GlucoseDataLayerContract.PATH_REFRESH_STATUS)
        assertEquals("/glucose/watch/ack", GlucoseDataLayerContract.PATH_WATCH_ACK)
        assertEquals("/watch/status/request", GlucoseDataLayerContract.PATH_WATCH_STATUS_REQUEST)
        assertEquals("/watch/status", GlucoseDataLayerContract.PATH_WATCH_STATUS)
    }

    @Test
    fun reading_keys_are_stable() {
        assertEquals("valueMgDl", GlucoseDataLayerContract.VALUE_MG_DL)
        assertEquals("trend", GlucoseDataLayerContract.TREND)
        assertEquals("deltaMgDl", GlucoseDataLayerContract.DELTA_MG_DL)
        assertEquals("timestampEpochMs", GlucoseDataLayerContract.TIMESTAMP_EPOCH_MS)
        assertEquals("stale", GlucoseDataLayerContract.STALE)
        assertEquals("displayUnit", GlucoseDataLayerContract.DISPLAY_UNIT)
        assertEquals("sequenceId", GlucoseDataLayerContract.SEQUENCE_ID)
        assertEquals("pushVersion", GlucoseDataLayerContract.PUSH_VERSION)
    }

    @Test
    fun ack_keys_are_stable() {
        assertEquals("ackReadingTimestampEpochMs", GlucoseDataLayerContract.ACK_READING_TIMESTAMP_EPOCH_MS)
        assertEquals("ackSequenceId", GlucoseDataLayerContract.ACK_SEQUENCE_ID)
        assertEquals("ackReceivedAt", GlucoseDataLayerContract.ACK_RECEIVED_AT)
    }

    @Test
    fun paths_are_absolute_and_distinct() {
        val paths =
            listOf(
                GlucoseDataLayerContract.PATH_LATEST,
                GlucoseDataLayerContract.PATH_REFRESH_REQUEST,
                GlucoseDataLayerContract.PATH_REFRESH_STATUS,
                GlucoseDataLayerContract.PATH_WATCH_ACK,
                GlucoseDataLayerContract.PATH_WATCH_STATUS_REQUEST,
                GlucoseDataLayerContract.PATH_WATCH_STATUS,
            )
        assertTrue(paths.all { it.startsWith("/") })
        assertEquals(paths.size, paths.toSet().size)
    }
}
