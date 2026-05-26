package com.widgetg7.mobile.sync

import com.widgetg7.core.datalayer.GlucoseDataLayerContract

/** Mobile aliases for Wear Data Layer glucose contract keys. */
object GlucoseKeys {
    const val PATH_LATEST = GlucoseDataLayerContract.PATH_LATEST
    const val PATH_REFRESH_REQUEST = GlucoseDataLayerContract.PATH_REFRESH_REQUEST
    const val PATH_REFRESH_STATUS = GlucoseDataLayerContract.PATH_REFRESH_STATUS
    const val PATH_WATCH_ACK = GlucoseDataLayerContract.PATH_WATCH_ACK
    const val PATH_WATCH_STATUS_REQUEST = GlucoseDataLayerContract.PATH_WATCH_STATUS_REQUEST
    const val PATH_WATCH_STATUS = GlucoseDataLayerContract.PATH_WATCH_STATUS

    const val VALUE_MG_DL = GlucoseDataLayerContract.VALUE_MG_DL
    const val TREND = GlucoseDataLayerContract.TREND
    const val DELTA_MG_DL = GlucoseDataLayerContract.DELTA_MG_DL
    const val TIMESTAMP_EPOCH_MS = GlucoseDataLayerContract.TIMESTAMP_EPOCH_MS
    const val STALE = GlucoseDataLayerContract.STALE
    const val SEQUENCE_ID = GlucoseDataLayerContract.SEQUENCE_ID
    const val TARGET_NODE_ID = GlucoseDataLayerContract.TARGET_NODE_ID
    const val REFRESH_STATUS = GlucoseDataLayerContract.REFRESH_STATUS
    const val REFRESH_MESSAGE = GlucoseDataLayerContract.REFRESH_MESSAGE
    const val REFRESH_UPDATED_AT = GlucoseDataLayerContract.REFRESH_UPDATED_AT
    const val REFRESH_IN_PROGRESS = GlucoseDataLayerContract.REFRESH_IN_PROGRESS
    const val REFRESH_COMPLETED = GlucoseDataLayerContract.REFRESH_COMPLETED
    const val REFRESH_FAILED = GlucoseDataLayerContract.REFRESH_FAILED
    const val WATCH_BATTERY_LEVEL = GlucoseDataLayerContract.WATCH_BATTERY_LEVEL
    const val WATCH_IS_CHARGING = GlucoseDataLayerContract.WATCH_IS_CHARGING
    const val WATCH_LOW_POWER = GlucoseDataLayerContract.WATCH_LOW_POWER
    const val WATCH_SYNC_LIMITED = GlucoseDataLayerContract.WATCH_SYNC_LIMITED
    const val WATCH_STATUS_MESSAGE = GlucoseDataLayerContract.WATCH_STATUS_MESSAGE
    const val WATCH_STATUS_UPDATED_AT = GlucoseDataLayerContract.WATCH_STATUS_UPDATED_AT
    const val WATCH_MANUFACTURER = GlucoseDataLayerContract.WATCH_MANUFACTURER
    const val WATCH_MODEL = GlucoseDataLayerContract.WATCH_MODEL
    const val WATCH_DEVICE = GlucoseDataLayerContract.WATCH_DEVICE
    const val WATCH_APP_INSTALLED = GlucoseDataLayerContract.WATCH_APP_INSTALLED
    const val WATCH_APP_VERSION_NAME = GlucoseDataLayerContract.WATCH_APP_VERSION_NAME
    const val WATCH_APP_VERSION_CODE = GlucoseDataLayerContract.WATCH_APP_VERSION_CODE
    const val WATCH_SUPPORTS_TILE = GlucoseDataLayerContract.WATCH_SUPPORTS_TILE
    const val WATCH_SUPPORTS_COMPLICATION = GlucoseDataLayerContract.WATCH_SUPPORTS_COMPLICATION
    const val WATCH_ACK_FAILURE_COUNT = GlucoseDataLayerContract.WATCH_ACK_FAILURE_COUNT
    const val SOURCE_PHONE_NODE_ID = GlucoseDataLayerContract.SOURCE_PHONE_NODE_ID
    const val ACK_READING_TIMESTAMP_EPOCH_MS = GlucoseDataLayerContract.ACK_READING_TIMESTAMP_EPOCH_MS
    const val ACK_SEQUENCE_ID = GlucoseDataLayerContract.ACK_SEQUENCE_ID
    const val ACK_RECEIVED_AT = GlucoseDataLayerContract.ACK_RECEIVED_AT
    const val PUSH_VERSION = GlucoseDataLayerContract.PUSH_VERSION

    const val INPUT_TRIGGERED_FROM_WATCH = "triggeredFromWatch"
}
