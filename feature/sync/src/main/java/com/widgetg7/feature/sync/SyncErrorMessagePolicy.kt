package com.widgetg7.feature.sync

/** Normalizes raw sync errors into user-facing French messages. */
object SyncErrorMessagePolicy {
    fun toUserMessage(rawMessage: String?, fallback: String = SyncMessageCatalog.UNKNOWN_ERROR): String {
        if (rawMessage.isNullOrBlank()) return fallback
        if (rawMessage.contains("timed out", ignoreCase = true)) {
            return SyncMessageCatalog.SYNC_TIMEOUT
        }
        return rawMessage
    }
}
