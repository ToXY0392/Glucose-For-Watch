package com.widgetg7.mobile.sync

object GlucoseKeys {
    const val PATH_LATEST = "/glucose/latest"

    const val VALUE_MG_DL = "valueMgDl"
    const val TREND = "trend"
    const val DELTA_MG_DL = "deltaMgDl"
    const val TIMESTAMP_EPOCH_MS = "timestampEpochMs"
    const val STALE = "stale"

    // Forces a new DataItem each push so listeners always receive updates.
    const val PUSH_VERSION = "pushVersion"
}
