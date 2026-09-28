package com.glucoseforwatch.feature.dexcomshare

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

            isSessionExpired(normalized) ->
                    sessionFailure(label)

            code in 500..599 ->
                DexcomShareException(DexcomShareErrorKind.NETWORK, "Dexcom est temporairement indisponible.")

            code == 401 || code == 403 ->
                DexcomShareException(DexcomShareErrorKind.AUTH, "Identifiants Dexcom invalides.")

            else ->
                DexcomShareException(DexcomShareErrorKind.UNKNOWN, "$label HTTP $code")
        }
    }

    fun sessionFailure(body: String, label: String): DexcomShareException? {
        return if (isSessionExpired(body.lowercase())) sessionFailure(label) else null
    }

    private fun sessionFailure(label: String) =
        DexcomShareException(DexcomShareErrorKind.SESSION, "$label : session Dexcom à renouveler.")

    private fun isSessionExpired(normalizedBody: String): Boolean =
        normalizedBody.contains("sessionidnotfound") ||
            normalizedBody.contains("session id not found")
}
