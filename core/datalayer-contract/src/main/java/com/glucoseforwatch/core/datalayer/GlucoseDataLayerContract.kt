package com.glucoseforwatch.core.datalayer

import androidx.annotation.Keep

enum class ReadingSource(val wireValue: String) {
    SHARE("SHARE"),
    DIRECT_G7("DIRECT_G7"),
}

enum class ReadingSourceEnvelopeRejection {
    INCOMPLETE_ENVELOPE,
    UNSUPPORTED_VERSION,
    UNKNOWN_SOURCE,
}

sealed interface ReadingSourceResolution {
    data class Accepted(
        val source: ReadingSource,
        val isLegacy: Boolean,
    ) : ReadingSourceResolution

    data class Rejected(val reason: ReadingSourceEnvelopeRejection) : ReadingSourceResolution
}

/**
 * Wear Data Layer paths and DataMap keys shared by phone and watch modules.
 * [PUSH_VERSION] bumps invalidate stale payloads on both sides.
 */
@Keep
object GlucoseDataLayerContract {
    const val PATH_LATEST = "/glucose/latest"
    const val PATH_REFRESH_REQUEST = "/glucose/refresh/request"
    const val PATH_REFRESH_STATUS = "/glucose/refresh/status"
    const val PATH_WATCH_ACK = "/glucose/watch/ack"
    const val PATH_WATCH_STATUS_REQUEST = "/watch/status/request"
    const val PATH_WATCH_STATUS = "/watch/status"

    const val VALUE_MG_DL = "valueMgDl"
    const val TREND = "trend"
    const val DELTA_MG_DL = "deltaMgDl"
    const val TIMESTAMP_EPOCH_MS = "timestampEpochMs"
    const val STALE = "stale"
    const val DISPLAY_UNIT = "displayUnit"
    const val SEQUENCE_ID = "sequenceId"
    const val TARGET_NODE_ID = "targetNodeId"
    const val SOURCE_PHONE_NODE_ID = "sourcePhoneNodeId"
    const val READING_SOURCE = "readingSource"
    const val SOURCE_ENVELOPE_VERSION = "sourceEnvelopeVersion"
    const val SOURCE_ENVELOPE_VERSION_1 = 1
    const val SOURCE_SHARE = "SHARE"
    const val SOURCE_DIRECT_G7 = "DIRECT_G7"
    const val REFRESH_STATUS = "refreshStatus"
    const val REFRESH_MESSAGE = "refreshMessage"
    const val REFRESH_UPDATED_AT = "refreshUpdatedAt"
    const val REFRESH_IN_PROGRESS = "in_progress"
    const val REFRESH_COMPLETED = "completed"
    const val REFRESH_FAILED = "failed"
    const val WATCH_BATTERY_LEVEL = "watchBatteryLevel"
    const val WATCH_IS_CHARGING = "watchIsCharging"
    const val WATCH_LOW_POWER = "watchLowPower"
    const val WATCH_SYNC_LIMITED = "watchSyncLimited"
    const val WATCH_STATUS_MESSAGE = "watchStatusMessage"
    const val WATCH_STATUS_UPDATED_AT = "watchStatusUpdatedAt"
    const val WATCH_MANUFACTURER = "watchManufacturer"
    const val WATCH_MODEL = "watchModel"
    const val WATCH_DEVICE = "watchDevice"
    const val WATCH_APP_INSTALLED = "watchAppInstalled"
    const val WATCH_APP_VERSION_NAME = "watchAppVersionName"
    const val WATCH_APP_VERSION_CODE = "watchAppVersionCode"
    const val WATCH_SUPPORTS_TILE = "watchSupportsTile"
    const val WATCH_SUPPORTS_COMPLICATION = "watchSupportsComplication"
    const val WATCH_ACK_FAILURE_COUNT = "watchAckFailureCount"
    const val ACK_READING_TIMESTAMP_EPOCH_MS = "ackReadingTimestampEpochMs"
    const val ACK_SEQUENCE_ID = "ackSequenceId"
    const val ACK_RECEIVED_AT = "ackReceivedAt"
    const val PUSH_VERSION = "pushVersion"

    /**
     * Resolves the additive source envelope without changing the existing payload.
     * Legacy payloads omit both fields and are treated as Share.
     */
    fun resolveReadingSource(schemaVersion: Int?, source: String?): ReadingSourceResolution {
        if (schemaVersion == null && source == null) {
            return ReadingSourceResolution.Accepted(
                source = ReadingSource.SHARE,
                isLegacy = true,
            )
        }
        if (schemaVersion == null || source == null) {
            return ReadingSourceResolution.Rejected(
                ReadingSourceEnvelopeRejection.INCOMPLETE_ENVELOPE,
            )
        }
        if (schemaVersion != SOURCE_ENVELOPE_VERSION_1) {
            return ReadingSourceResolution.Rejected(
                ReadingSourceEnvelopeRejection.UNSUPPORTED_VERSION,
            )
        }
        val resolvedSource = when (source) {
            SOURCE_SHARE -> ReadingSource.SHARE
            SOURCE_DIRECT_G7 -> ReadingSource.DIRECT_G7
            else -> null
        } ?: return ReadingSourceResolution.Rejected(
            ReadingSourceEnvelopeRejection.UNKNOWN_SOURCE,
        )
        return ReadingSourceResolution.Accepted(source = resolvedSource, isLegacy = false)
    }
}
