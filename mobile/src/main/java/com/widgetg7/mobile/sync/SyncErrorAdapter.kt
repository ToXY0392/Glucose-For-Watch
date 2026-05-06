package com.widgetg7.mobile.sync

import com.widgetg7.core.model.SyncErrorCategory
import com.widgetg7.feature.dexcomshare.DexcomShareErrorKind
import com.widgetg7.feature.dexcomshare.DexcomShareException
import com.widgetg7.feature.sync.SyncErrorMessagePolicy
import com.widgetg7.feature.sync.SyncErrorPolicy

object SyncErrorAdapter {
    fun toCategory(t: Throwable): SyncErrorCategory {
        SyncErrorPolicy.fromTimeoutHint(t.message)?.let { return it }
        if (t is DexcomShareException) {
            val kind = when (t.kind) {
                DexcomShareErrorKind.AUTH -> "AUTH"
                DexcomShareErrorKind.SESSION -> "SESSION"
                DexcomShareErrorKind.NETWORK -> "NETWORK"
                DexcomShareErrorKind.NO_DATA -> "NO_DATA"
                DexcomShareErrorKind.UNKNOWN -> "UNKNOWN"
            }
            return SyncErrorPolicy.fromDexcomKind(kind)
        }
        return SyncErrorCategory.OTHER
    }

    fun toUserMessage(t: Throwable): String {
        return when {
            t is DexcomShareException -> t.message
            else -> SyncErrorMessagePolicy.toUserMessage(t.message)
        }
    }
}
