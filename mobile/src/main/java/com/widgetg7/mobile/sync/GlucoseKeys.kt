package com.widgetg7.mobile.sync

object GlucoseKeys {
    const val PATH_LATEST = "/glucose/latest"
    const val PATH_REFRESH_REQUEST = "/glucose/refresh/request"
    const val PATH_REFRESH_STATUS = "/glucose/refresh/status"
    const val PATH_WATCH_ACK = "/glucose/watch/ack"
    const val PATH_WATCH_STATUS = "/watch/status"

    const val VALUE_MG_DL = "valueMgDl"
    const val TREND = "trend"
    const val DELTA_MG_DL = "deltaMgDl"
    const val TIMESTAMP_EPOCH_MS = "timestampEpochMs"
    const val STALE = "stale"
    const val SEQUENCE_ID = "sequenceId"
    const val REFRESH_STATUS = "refreshStatus"
    const val REFRESH_MESSAGE = "refreshMessage"
    const val REFRESH_UPDATED_AT = "refreshUpdatedAt"
    const val REFRESH_IN_PROGRESS = "in_progress"
    const val REFRESH_COMPLETED = "completed"
    const val REFRESH_FAILED = "failed"
    const val WATCH_BATTERY_LEVEL = "watchBatteryLevel"
    const val WATCH_LOW_POWER = "watchLowPower"
    const val WATCH_SYNC_LIMITED = "watchSyncLimited"
    const val WATCH_STATUS_MESSAGE = "watchStatusMessage"
    const val WATCH_STATUS_UPDATED_AT = "watchStatusUpdatedAt"
    const val WATCH_MANUFACTURER = "watchManufacturer"
    const val WATCH_MODEL = "watchModel"
    const val WATCH_DEVICE = "watchDevice"
    const val ACK_READING_TIMESTAMP_EPOCH_MS = "ackReadingTimestampEpochMs"
    const val ACK_SEQUENCE_ID = "ackSequenceId"
    const val ACK_RECEIVED_AT = "ackReceivedAt"

    // Forces a new DataItem each push so listeners always receive updates.
    const val PUSH_VERSION = "pushVersion"

    const val INPUT_TRIGGERED_FROM_WATCH = "triggeredFromWatch"
}
