package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class BudgetDto(
    val id: String,
    val category: String,
    val amount: BigDecimal,
    val currency: String,
    val period: String,
    val start_date: String,
    val end_date: String,
    val spent: BigDecimal,
    val remaining: BigDecimal,
    val created_at: String,
    val updated_at: String
)

@JsonClass(generateAdapter = true)
data class BudgetCreateRequestDto(
    val category: String,
    val amount: BigDecimal,
    val currency: String,
    val start_date: String,
    val end_date: String
)
