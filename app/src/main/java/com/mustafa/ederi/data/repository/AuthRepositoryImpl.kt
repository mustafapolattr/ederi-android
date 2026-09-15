package com.mustafa.ederi.data.repository

import com.mustafa.ederi.core.error.safeApiCall
import com.mustafa.ederi.core.error.safeUnitApiCall
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.core.security.SessionManager
import com.mustafa.ederi.core.security.TokenStorage
import com.mustafa.ederi.data.remote.AuthApiService
import com.mustafa.ederi.data.remote.dto.LoginRequestDto
import com.mustafa.ederi.data.remote.dto.LogoutRequestDto
import com.mustafa.ederi.data.remote.dto.RegisterRequestDto
import com.mustafa.ederi.domain.repository.AuthRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
    private val moshi: Moshi
) : AuthRepository {

    override val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedIn

    override suspend fun register(
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ): NetworkResult<Unit> {
        val registerResult = safeApiCall(moshi) {
            authApiService.register(RegisterRequestDto(email, password, firstName, lastName))
        }
        if (registerResult is NetworkResult.Error) return NetworkResult.Error(registerResult.error)
        return login(email, password)
    }

    override suspend fun login(email: String, password: String): NetworkResult<Unit> {
        return when (val result = safeApiCall(moshi) { authApiService.login(LoginRequestDto(email, password)) }) {
            is NetworkResult.Success -> {
                tokenStorage.saveTokens(result.data.access, result.data.refresh)
                sessionManager.setLoggedIn()
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }

    override suspend fun logout(): NetworkResult<Unit> {
        val refreshToken = tokenStorage.getRefreshToken()
        val result = if (refreshToken != null) {
            safeUnitApiCall(moshi) { authApiService.logout(LogoutRequestDto(refreshToken)) }
        } else {
            NetworkResult.Success(Unit)
        }
        tokenStorage.clear()
        sessionManager.setLoggedOut()
        return result
    }
}
