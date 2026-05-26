package com.widgetg7.feature.dexcomshare

internal object DexcomShareHttpClassifier {
    fun classifyFailure(code: Int, body: String, label: String): DexcomShareException {
        val normalized = body.lowercase()
        return when {
            normalized.contains("accountpasswordinvalid") ||
                normalized.contains("invalidpassword") ||
                normalized.contains("invalid password") ||
                normalized.contains("account not found") ||
                normalized.contains("authenticatepublisheraccount") ->
                DexcomShareException(DexcomShareErrorKind.AUTH, "Identifiants Dexcom invalides.")

            normalized.contains("sessionidnotfound") ->
                DexcomShareException(DexcomShareErrorKind.SESSION, "Session Dexcom à renouveler.")

            code in 500..599 ->
                DexcomShareException(DexcomShareErrorKind.NETWORK, "Dexcom est temporairement indisponible.")

            code == 401 || code == 403 ->
                DexcomShareException(DexcomShareErrorKind.AUTH, "Identifiants Dexcom invalides.")

            else ->
                DexcomShareException(DexcomShareErrorKind.UNKNOWN, "$label HTTP $code")
        }
    }
}
