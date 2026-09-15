package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class AccountDto(
    val id: String,
    val name: String,
    val type: String,
    val currency: String,
    val initial_balance: BigDecimal,
    val current_balance: BigDecimal,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)

@JsonClass(generateAdapter = true)
data class AccountCreateRequestDto(
    val name: String,
    val type: String,
    val currency: String,
    val initial_balance: BigDecimal
)

/** currency/initial_balance are immutable after creation — not patchable. */
@JsonClass(generateAdapter = true)
data class AccountUpdateRequestDto(
    val name: String? = null,
    val type: String? = null
)
