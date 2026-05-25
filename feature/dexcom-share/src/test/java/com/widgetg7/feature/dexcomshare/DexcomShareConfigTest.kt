package com.widgetg7.feature.dexcomshare

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DexcomShareConfigTest {
    @Test
    fun baseUrl_usPointsToShare2() {
        val config = DexcomShareConfig(
            username = "user",
            password = "pass",
            server = "US",
            applicationId = "app-id",
        )
        assertEquals("https://share2.dexcom.com", config.baseUrl())
    }

    @Test
    fun baseUrl_ousPointsToShareOus1() {
        val config = DexcomShareConfig(
            username = "user",
            password = "pass",
            server = "OUS",
            applicationId = "app-id",
        )
        assertEquals("https://shareous1.dexcom.com", config.baseUrl())
    }

    @Test
    fun isConfigured_falseWhenServerInvalid() {
        val config = DexcomShareConfig(
            username = "user",
            password = "pass",
            server = "EU",
            applicationId = "app-id",
        )
        assertFalse(config.isConfigured())
    }

    @Test
    fun isConfigured_trueWhenAllFieldsPresent() {
        val config = DexcomShareConfig(
            username = "user",
            password = "pass",
            server = "us",
            applicationId = "app-id",
        )
        assertTrue(config.isConfigured())
    }
}
