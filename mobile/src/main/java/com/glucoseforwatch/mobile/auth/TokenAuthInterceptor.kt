package com.glucoseforwatch.mobile.auth

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that injects the Authorization header with the bearer token when available.
 * Token retrieval is delegated to TokenProvider so storage implementation is decoupled.
 */
class TokenAuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenProvider.get(context).getAccessToken()
        val request = if (token.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }
}
