package com.widgetg7.feature.sync

import com.widgetg7.core.model.SyncErrorCategory

object SyncErrorPolicy {
    fun fromTimeoutHint(message: String?): SyncErrorCategory? {
        if (message.isNullOrBlank()) return null
        return if (message.contains("timed out", ignoreCase = true)) {
            SyncErrorCategory.NETWORK
        } else {
            null
        }
    }

    fun fromDexcomKind(kind: String?): SyncErrorCategory {
        return when (kind?.uppercase()) {
            "AUTH" -> SyncErrorCategory.AUTH
            "SESSION", "NETWORK" -> SyncErrorCategory.NETWORK
            else -> SyncErrorCategory.OTHER
        }
    }
}
