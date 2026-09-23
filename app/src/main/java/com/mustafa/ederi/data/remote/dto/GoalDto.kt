package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class GoalDto(
    val id: String,
    val name: String,
    val goal_type: String,
    val target_amount: BigDecimal,
    val current_amount: BigDecimal,
    val currency: String,
    val target_date: String,
    val remaining_amount: BigDecimal,
    val required_monthly_contribution: BigDecimal,
    val created_at: String,
    val updated_at: String
)

@JsonClass(generateAdapter = true)
data class GoalCreateRequestDto(
    val name: String,
    val goal_type: String,
    val target_amount: BigDecimal,
    val current_amount: BigDecimal,
    val currency: String,
    val target_date: String
)
