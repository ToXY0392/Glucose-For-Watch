package com.widgetg7.feature.sync

/** Outcome of the optional watch push attempt during a sync pass. */
enum class WatchDeliveryStatus {
    /** No push was attempted (no new reading, no force, no pending retry). */
    NOT_APPLICABLE,

    /** pushLatest returned true. */
    DELIVERED,

    /** pushLatest failed and the reading was stored in [PendingPushPort]. */
    QUEUED,

    /** pushLatest failed and no pending queue is available. */
    WATCH_UNAVAILABLE,
}
