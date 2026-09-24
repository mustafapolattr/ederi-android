package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class DashboardResponseDto(
    val success: Boolean,
    val data: DashboardDataDto
)

@JsonClass(generateAdapter = true)
data class DashboardDataDto(
    val total_balance: List<CurrencyAmountDto>,
    val this_month: List<MonthSummaryDto>,
    val budgets: List<DashboardBudgetDto>,
    val goals: List<DashboardGoalDto>,
    val forecast: List<ForecastDto>,
    val available_to_spend: List<AvailableToSpendDto>
)

@JsonClass(generateAdapter = true)
data class ForecastDto(
    val currency: String,
    val forecast_balance: BigDecimal
)

@JsonClass(generateAdapter = true)
data class AvailableToSpendDto(
    val currency: String,
    val available_to_spend: BigDecimal
)

@JsonClass(generateAdapter = true)
data class CurrencyAmountDto(
    val currency: String,
    val amount: BigDecimal
)

@JsonClass(generateAdapter = true)
data class MonthSummaryDto(
    val currency: String,
    val income: BigDecimal,
    val expense: BigDecimal,
    val saved: BigDecimal
)

/** Unlike [BudgetDto], `category` here is the display name, not an id (see apps/core/services.py). */
@JsonClass(generateAdapter = true)
data class DashboardBudgetDto(
    val id: String,
    val category: String,
    val amount: BigDecimal,
    val currency: String,
    val spent: BigDecimal,
    val remaining: BigDecimal
)

@JsonClass(generateAdapter = true)
data class DashboardGoalDto(
    val id: String,
    val name: String,
    val target_amount: BigDecimal,
    val current_amount: BigDecimal,
    val currency: String,
    val target_date: String,
    val remaining_amount: BigDecimal,
    val required_monthly_contribution: BigDecimal
)
