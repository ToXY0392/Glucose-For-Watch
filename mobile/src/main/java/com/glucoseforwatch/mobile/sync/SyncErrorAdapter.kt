package com.glucoseforwatch.mobile.sync

import com.glucoseforwatch.core.model.SyncErrorCategory
import com.glucoseforwatch.feature.dexcomshare.DexcomShareErrorKind
import com.glucoseforwatch.feature.dexcomshare.DexcomShareException
import com.glucoseforwatch.feature.sync.SyncErrorMessagePolicy
import com.glucoseforwatch.feature.sync.SyncErrorPolicy

/** Maps Dexcom and sync exceptions to categories and user-facing messages. */
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
