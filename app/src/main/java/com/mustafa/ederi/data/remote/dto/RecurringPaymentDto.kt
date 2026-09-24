package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class RecurringPaymentDto(
    val id: String,
    val name: String,
    val type: String,
    val amount: BigDecimal,
    val currency: String,
    val frequency: String,
    val next_payment_date: String,
    val category: String?,
    val account: String,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)

@JsonClass(generateAdapter = true)
data class RecurringPaymentCreateRequestDto(
    val name: String,
    val type: String,
    val amount: BigDecimal,
    val currency: String,
    val frequency: String,
    val next_payment_date: String,
    val category: String?,
    val account: String
)
