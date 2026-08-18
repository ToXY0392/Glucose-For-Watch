package com.glucoseforwatch.core.model

import androidx.annotation.Keep

/** User-facing glucose concentration unit (internal storage stays mg/dL). */
@Keep
enum class GlucoseDisplayUnit {
    MG_DL,
    MMOL_L,
    ;

    fun label(): String =
        when (this) {
            MG_DL -> "mg/dL"
            MMOL_L -> "mmol/L"
        }

    companion object {
        fun fromStorage(value: String?): GlucoseDisplayUnit =
            when (value?.trim()?.uppercase()) {
                MMOL_L.name, "MMOL/L" -> MMOL_L
                else -> MG_DL
            }
    }
}
