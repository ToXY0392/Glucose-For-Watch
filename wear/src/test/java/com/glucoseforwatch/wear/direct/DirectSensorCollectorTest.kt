package com.glucoseforwatch.wear.direct

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test

class DirectSensorCollectorTest {
    @Test
    fun refuses_to_start_without_configuration_or_permission() {
        val transport = FakeBleTransport()
        val collector = DirectSensorCollector(transport)

        collector.start(isConfigured = false, hasBluetoothPermission = true)
        assertEquals(DirectCollectorState.Unconfigured, collector.state)
        assertEquals(0, transport.startCount)

        collector.start(isConfigured = true, hasBluetoothPermission = false)
        assertEquals(DirectCollectorState.PermissionRequired, collector.state)
        assertEquals(0, transport.startCount)
    }

    @Test
    fun fake_transport_drives_scan_connect_and_measurement_states() {
        val transport = FakeBleTransport()
        val collector = DirectSensorCollector(transport)

        collector.start(isConfigured = true, hasBluetoothPermission = true)
        assertEquals(DirectCollectorState.Scanning, collector.state)
        assertEquals(1, transport.startCount)

        transport.emit(DirectTransportEvent.DeviceFound)
        assertEquals(DirectCollectorState.Connecting, collector.state)

        transport.emit(DirectTransportEvent.Connected)
        assertEquals(DirectCollectorState.Connected, collector.state)

        transport.emit(DirectTransportEvent.MeasurementReceived(timestampEpochMs = 1_000L))
        assertEquals(DirectCollectorState.Receiving(lastMeasurementAtEpochMs = 1_000L), collector.state)
    }

    @Test
    fun disconnect_can_recover_through_a_new_scan_and_connection() {
        val transport = FakeBleTransport()
        val collector = DirectSensorCollector(transport)
        collector.start(isConfigured = true, hasBluetoothPermission = true)

        transport.emit(DirectTransportEvent.DeviceFound)
        transport.emit(DirectTransportEvent.Connected)
        transport.emit(DirectTransportEvent.Disconnected)
        assertEquals(DirectCollectorState.Disconnected, collector.state)

        transport.emit(DirectTransportEvent.ScanStarted)
        transport.emit(DirectTransportEvent.DeviceFound)
        transport.emit(DirectTransportEvent.Connected)
        assertEquals(DirectCollectorState.Connected, collector.state)
    }

    @Test
    fun invalid_or_unconnected_measurement_events_are_not_accepted_as_receiving() {
        val transport = FakeBleTransport()
        val collector = DirectSensorCollector(transport)
        collector.start(isConfigured = true, hasBluetoothPermission = true)

        transport.emit(DirectTransportEvent.MeasurementReceived(timestampEpochMs = 1_000L))
        assertEquals(
            DirectCollectorState.RecoverableError("measurement_before_connection"),
            collector.state,
        )

        transport.emit(DirectTransportEvent.Connected)
        transport.emit(DirectTransportEvent.MeasurementReceived(timestampEpochMs = 0L))
        assertEquals(
            DirectCollectorState.RecoverableError("invalid_measurement_timestamp"),
            collector.state,
        )
    }

    @Test
    fun unsupported_and_recoverable_transport_events_remain_explicit() {
        val transport = FakeBleTransport()
        val collector = DirectSensorCollector(transport)
        collector.start(isConfigured = true, hasBluetoothPermission = true)

        transport.emit(DirectTransportEvent.Unsupported("sensor_generation_unsupported"))
        assertEquals(DirectCollectorState.Unsupported("sensor_generation_unsupported"), collector.state)

        transport.emit(DirectTransportEvent.RecoverableError("connection_timeout"))
        assertEquals(DirectCollectorState.RecoverableError("connection_timeout"), collector.state)
    }

    @Test
    fun stop_ends_transport_and_ignores_late_callbacks() {
        val transport = FakeBleTransport()
        val collector = DirectSensorCollector(transport)
        collector.start(isConfigured = true, hasBluetoothPermission = true)

        collector.stop()
        assertEquals(1, transport.stopCount)
        assertEquals(DirectCollectorState.Disconnected, collector.state)

        transport.emit(DirectTransportEvent.Connected)
        assertEquals(DirectCollectorState.Disconnected, collector.state)

        collector.stop()
        assertEquals(1, transport.stopCount)

        collector.start(isConfigured = true, hasBluetoothPermission = true)
        assertEquals(2, transport.startCount)
        transport.emit(DirectTransportEvent.Connected)
        assertEquals(DirectCollectorState.Connected, collector.state)
    }

    @Test
    fun repeated_start_does_not_start_a_second_transport() {
        val transport = FakeBleTransport()
        val collector = DirectSensorCollector(transport)

        collector.start(isConfigured = true, hasBluetoothPermission = true)
        collector.start(isConfigured = true, hasBluetoothPermission = true)

        assertEquals(DirectCollectorState.Scanning, collector.state)
        assertEquals(1, transport.startCount)
    }

    @Test
    fun transport_start_failure_is_reported_and_a_later_start_can_retry() {
        val transport = FakeBleTransport()
        val collector = DirectSensorCollector(transport)
        transport.startFailure = IllegalStateException("synthetic start failure")

        try {
            collector.start(isConfigured = true, hasBluetoothPermission = true)
            fail("Expected the transport start failure to be surfaced")
        } catch (expected: IllegalStateException) {
            assertEquals("synthetic start failure", expected.message)
        }
        assertEquals(
            DirectCollectorState.RecoverableError("transport_start_failed"),
            collector.state,
        )

        transport.startFailure = null
        collector.start(isConfigured = true, hasBluetoothPermission = true)

        assertEquals(2, transport.startCount)
        assertEquals(DirectCollectorState.Scanning, collector.state)
    }

    private class FakeBleTransport : DirectSensorTransport {
        private var listener: ((DirectTransportEvent) -> Unit)? = null
        var startFailure: RuntimeException? = null
        var startCount = 0
            private set
        var stopCount = 0
            private set

        override fun start(onEvent: (DirectTransportEvent) -> Unit) {
            startCount++
            startFailure?.let { throw it }
            listener = onEvent
        }

        override fun stop() {
            stopCount++
        }

        fun emit(event: DirectTransportEvent) {
            assertFalse("Fake transport has not been started", listener == null)
            listener?.invoke(event)
        }
    }
}
