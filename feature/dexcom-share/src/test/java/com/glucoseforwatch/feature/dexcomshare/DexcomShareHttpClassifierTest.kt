package com.glucoseforwatch.feature.dexcomshare

import org.junit.Assert.assertEquals
import org.junit.Test

class DexcomShareHttpClassifierTest {
    @Test
    fun classifyFailure_401AccountPasswordInvalid_isAuth() {
        val error = DexcomShareHttpClassifier.classifyFailure(
            401,
            """{"Code":"AccountPasswordInvalid"}""",
            "Dexcom Share auth",
        )
        assertEquals(DexcomShareErrorKind.AUTH, error.kind)
    }

    @Test
    fun classifyFailure_403Forbidden_isAuth() {
        val error = DexcomShareHttpClassifier.classifyFailure(
            403,
            "Forbidden",
            "Dexcom Share auth",
        )
        assertEquals(DexcomShareErrorKind.AUTH, error.kind)
    }

    @Test
    fun classifyFailure_sessionIdNotFound_isSession() {
        val error = DexcomShareHttpClassifier.classifyFailure(
            200,
            "SessionIdNotFound",
            "Dexcom Share read",
        )
        assertEquals(DexcomShareErrorKind.SESSION, error.kind)
    }

    @Test
    fun classifyFailure_503_isNetwork() {
        val error = DexcomShareHttpClassifier.classifyFailure(
            503,
            "Service Unavailable",
            "Dexcom Share read",
        )
        assertEquals(DexcomShareErrorKind.NETWORK, error.kind)
    }

    @Test
    fun classifyFailure_unknownBody_isUnknown() {
        val error = DexcomShareHttpClassifier.classifyFailure(
            418,
            "Teapot",
            "Dexcom Share read",
        )
        assertEquals(DexcomShareErrorKind.UNKNOWN, error.kind)
        assertEquals("Dexcom Share read HTTP 418", error.message)
    }
}
