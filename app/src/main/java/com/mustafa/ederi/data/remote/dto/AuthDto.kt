package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val first_name: String? = null,
    val last_name: String? = null
)

/** Register does not return tokens — only the created user (password is write_only). */
@JsonClass(generateAdapter = true)
data class RegisterResponseDto(
    val id: String,
    val email: String,
    val first_name: String?,
    val last_name: String?
)

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    val email: String,
    val password: String
)

/** Shared by /login and /login/refresh — refresh rotates and returns a new refresh token too. */
@JsonClass(generateAdapter = true)
data class TokenPairDto(
    val access: String,
    val refresh: String
)

@JsonClass(generateAdapter = true)
data class RefreshRequestDto(val refresh: String)

@JsonClass(generateAdapter = true)
data class LogoutRequestDto(val refresh: String)
