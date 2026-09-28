package com.glucoseforwatch.mobile.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Simple Robolectric test to validate migration from legacy SharedPreferences to
 * EncryptedSharedPreferences-backed repository.
 */
@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(sdk = [33])
class EncryptedTokenRepositoryTest {
    @Test
    fun `migrates legacy tokens into encrypted storage and clears legacy prefs`() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val legacyName = "gfw_tokens"
        val legacy = ctx.getSharedPreferences(legacyName, Context.MODE_PRIVATE)
        legacy.edit().clear().commit()

        // write legacy tokens
        legacy.edit().putString("access_token", "legacy_access").putString("refresh_token", "legacy_refresh").commit()

        // instantiate repository - migration should run in init
        val testEncrypted = ctx.getSharedPreferences("test_encrypted", Context.MODE_PRIVATE)
        val repo = EncryptedTokenRepository(ctx, testEncrypted)

        // assert tokens are accessible via repo
        assertEquals("legacy_access", repo.getAccessToken())
        assertEquals("legacy_refresh", repo.getRefreshToken())

        // legacy prefs should be cleared by migration
        val legacyAfter = ctx.getSharedPreferences(legacyName, Context.MODE_PRIVATE)
        assertFalse(legacyAfter.contains("access_token"))
        assertFalse(legacyAfter.contains("refresh_token"))
    }
}