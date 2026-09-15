package com.mustafa.ederi.core.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for whether the UI should show the auth graph or
 * the main graph. Updated on login/register/logout, and on a forced logout
 * from [com.mustafa.ederi.core.network.TokenAuthenticator] when the refresh
 * token itself has expired or been blacklisted.
 */
@Singleton
class SessionManager @Inject constructor(
    tokenStorage: TokenStorage
) {
    private val _isLoggedIn = MutableStateFlow(tokenStorage.getAccessToken() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun setLoggedIn() {
        _isLoggedIn.value = true
    }

    fun setLoggedOut() {
        _isLoggedIn.value = false
    }
}
