package com.glucoseforwatch.wear.sync

import kotlinx.coroutines.delay

/** Retries watch-to-phone ACK writes with linear backoff. */
internal class WearAckSender(
    private val maxAttempts: Int = MAX_ACK_ATTEMPTS,
    private val retryDelayMs: Long = ACK_RETRY_MS,
    private val delayFn: suspend (Long) -> Unit = { delay(it) },
) {
    suspend fun send(putAck: suspend () -> Unit): Boolean {
        repeat(maxAttempts) { attempt ->
            val result = runCatching { putAck() }
            if (result.isSuccess) return true
            if (attempt < maxAttempts - 1) {
                delayFn(retryDelayMs * (attempt + 1))
            }
        }
        return false
    }

    companion object {
        const val MAX_ACK_ATTEMPTS = 3
        const val ACK_RETRY_MS = 500L
    }
}
