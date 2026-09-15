package com.mustafa.ederi.data.remote

import com.mustafa.ederi.data.remote.dto.LoginRequestDto
import com.mustafa.ederi.data.remote.dto.LogoutRequestDto
import com.mustafa.ederi.data.remote.dto.RefreshRequestDto
import com.mustafa.ederi.data.remote.dto.RegisterRequestDto
import com.mustafa.ederi.data.remote.dto.RegisterResponseDto
import com.mustafa.ederi.data.remote.dto.TokenPairDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Unauthenticated endpoints only — served by a Retrofit client with no
 * [com.mustafa.ederi.core.network.AuthInterceptor]/
 * [com.mustafa.ederi.core.network.TokenAuthenticator], so a token refresh
 * never recurses into itself.
 */
interface AuthApiService {
    @POST("auth/register/")
    suspend fun register(@Body request: RegisterRequestDto): Response<RegisterResponseDto>

    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequestDto): Response<TokenPairDto>

    @POST("auth/login/refresh/")
    suspend fun refresh(@Body request: RefreshRequestDto): Response<TokenPairDto>

    @POST("auth/logout/")
    suspend fun logout(@Body request: LogoutRequestDto): Response<Unit>
}
