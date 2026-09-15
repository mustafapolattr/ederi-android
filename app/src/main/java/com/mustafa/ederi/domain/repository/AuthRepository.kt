package com.mustafa.ederi.domain.repository

import com.mustafa.ederi.core.network.NetworkResult
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isLoggedIn: StateFlow<Boolean>

    /** Registers the user, then logs in with the same credentials since register does not return tokens. */
    suspend fun register(
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ): NetworkResult<Unit>

    suspend fun login(email: String, password: String): NetworkResult<Unit>

    suspend fun logout(): NetworkResult<Unit>
}
