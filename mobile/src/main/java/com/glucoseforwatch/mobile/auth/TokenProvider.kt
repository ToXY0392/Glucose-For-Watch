package com.glucoseforwatch.mobile.auth

import android.content.Context
import com.glucoseforwatch.core.auth.TokenRepository

/**
 * Lightweight provider/factory for TokenRepository. Keeps wiring local and simple. If the project
 * later adopts DI (Hilt/Koin), update this provider accordingly.
 */
object TokenProvider {
    @Volatile
    private var instance: TokenRepository? = null

    fun get(context: Context): TokenRepository = instance ?: synchronized(this) {
        instance ?: EncryptedTokenRepository(context.applicationContext).also { instance = it }
    }
}
