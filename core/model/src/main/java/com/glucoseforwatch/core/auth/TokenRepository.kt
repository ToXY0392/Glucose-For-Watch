package com.glucoseforwatch.core.auth

/**
 * Abstraction for storing OAuth tokens. Implementations should persist access and refresh tokens
 * and expose simple operations for consumers.
 */
interface TokenRepository {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveTokens(accessToken: String, refreshToken: String)
    fun clearTokens()
    fun hasToken(): Boolean
}