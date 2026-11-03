package com.glucoseforwatch.wear.direct

internal sealed interface DirectCollectorState {
    data object Unconfigured : DirectCollectorState
    data object PermissionRequired : DirectCollectorState
    data object Scanning : DirectCollectorState
    data object Connecting : DirectCollectorState
    data object Connected : DirectCollectorState
    data class Receiving(val lastMeasurementAtEpochMs: Long) : DirectCollectorState
    data object Disconnected : DirectCollectorState
    data class Unsupported(val reasonCode: String) : DirectCollectorState
    data class RecoverableError(val reasonCode: String) : DirectCollectorState
}

internal sealed interface DirectTransportEvent {
    data object ScanStarted : DirectTransportEvent
    data object DeviceFound : DirectTransportEvent
    data object Connected : DirectTransportEvent
    data class MeasurementReceived(val timestampEpochMs: Long) : DirectTransportEvent
    data object Disconnected : DirectTransportEvent
    data class Unsupported(val reasonCode: String) : DirectTransportEvent
    data class RecoverableError(val reasonCode: String) : DirectTransportEvent
}

internal interface DirectSensorTransport {
    fun start(onEvent: (DirectTransportEvent) -> Unit)
    fun stop()
}

internal class DirectSensorCollector(
    private val transport: DirectSensorTransport,
    private val onStateChanged: (DirectCollectorState) -> Unit = {},
) {
    var state: DirectCollectorState = DirectCollectorState.Unconfigured
        private set

    private var active = false

    @Synchronized
    fun start(isConfigured: Boolean, hasBluetoothPermission: Boolean) {
        if (active) return
        when {
            !isConfigured -> transition(DirectCollectorState.Unconfigured)
            !hasBluetoothPermission -> transition(DirectCollectorState.PermissionRequired)
            else -> {
                active = true
                transition(DirectCollectorState.Scanning)
                try {
                    transport.start(::onTransportEvent)
                } catch (error: RuntimeException) {
                    active = false
                    transition(DirectCollectorState.RecoverableError("transport_start_failed"))
                    throw error
                }
            }
        }
    }

    @Synchronized
    fun stop() {
        if (!active) return
        try {
            transport.stop()
        } finally {
            active = false
            transition(DirectCollectorState.Disconnected)
        }
    }

    @Synchronized
    private fun onTransportEvent(event: DirectTransportEvent) {
        if (!active) return
        val next = when (event) {
            DirectTransportEvent.ScanStarted -> DirectCollectorState.Scanning
            DirectTransportEvent.DeviceFound -> DirectCollectorState.Connecting
            DirectTransportEvent.Connected -> DirectCollectorState.Connected
            is DirectTransportEvent.MeasurementReceived -> when {
                event.timestampEpochMs <= 0 ->
                    DirectCollectorState.RecoverableError("invalid_measurement_timestamp")
                state !is DirectCollectorState.Connected && state !is DirectCollectorState.Receiving ->
                    DirectCollectorState.RecoverableError("measurement_before_connection")
                else -> DirectCollectorState.Receiving(event.timestampEpochMs)
            }
            DirectTransportEvent.Disconnected -> DirectCollectorState.Disconnected
            is DirectTransportEvent.Unsupported -> DirectCollectorState.Unsupported(event.reasonCode)
            is DirectTransportEvent.RecoverableError ->
                DirectCollectorState.RecoverableError(event.reasonCode)
        }
        transition(next)
    }

    private fun transition(next: DirectCollectorState) {
        state = next
        onStateChanged(next)
    }
}
