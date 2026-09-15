package com.mustafa.ederi.core.network

import com.mustafa.ederi.core.security.SessionManager
import com.mustafa.ederi.core.security.TokenStorage
import com.mustafa.ederi.data.remote.AuthApiService
import com.mustafa.ederi.data.remote.dto.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Refreshes the access token on a 401. The backend rotates+blacklists
 * refresh tokens on every use, so a successful refresh must overwrite BOTH
 * stored tokens, not just the access token.
 *
 * Synchronized so concurrent 401s don't each attempt a refresh — the second
 * caller sees the token the first one just saved and retries with that
 * instead of burning (and blacklisting) another rotation.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        synchronized(this) {
            val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val currentAccessToken = tokenStorage.getAccessToken()

            if (currentAccessToken != null && currentAccessToken != failedToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            val refreshToken = tokenStorage.getRefreshToken()
            if (refreshToken == null) {
                forceLogout()
                return null
            }

            val refreshResponse = runBlocking {
                runCatching { authApiService.refresh(RefreshRequestDto(refreshToken)) }.getOrNull()
            }
            val tokens = refreshResponse?.takeIf { it.isSuccessful }?.body()

            if (tokens == null) {
                forceLogout()
                return null
            }

            tokenStorage.saveTokens(tokens.access, tokens.refresh)

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${tokens.access}")
                .build()
        }
    }

    private fun forceLogout() {
        tokenStorage.clear()
        sessionManager.setLoggedOut()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
