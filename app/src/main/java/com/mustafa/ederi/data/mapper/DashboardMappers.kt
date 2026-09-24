package com.mustafa.ederi.data.mapper

import com.mustafa.ederi.data.remote.dto.AvailableToSpendDto
import com.mustafa.ederi.data.remote.dto.CurrencyAmountDto
import com.mustafa.ederi.data.remote.dto.DashboardBudgetDto
import com.mustafa.ederi.data.remote.dto.DashboardDataDto
import com.mustafa.ederi.data.remote.dto.DashboardGoalDto
import com.mustafa.ederi.data.remote.dto.ForecastDto
import com.mustafa.ederi.data.remote.dto.MonthSummaryDto
import com.mustafa.ederi.domain.model.AvailableToSpend
import com.mustafa.ederi.domain.model.CurrencyAmount
import com.mustafa.ederi.domain.model.DashboardBudget
import com.mustafa.ederi.domain.model.DashboardData
import com.mustafa.ederi.domain.model.DashboardGoal
import com.mustafa.ederi.domain.model.MonthEndForecast
import com.mustafa.ederi.domain.model.MonthSummary

fun CurrencyAmountDto.toDomain() = CurrencyAmount(currency = currency, amount = amount)

fun MonthSummaryDto.toDomain() = MonthSummary(currency = currency, income = income, expense = expense, saved = saved)

fun DashboardBudgetDto.toDomain() = DashboardBudget(
    id = id,
    category = category,
    amount = amount,
    currency = currency,
    spent = spent,
    remaining = remaining
)

fun DashboardGoalDto.toDomain() = DashboardGoal(
    id = id,
    name = name,
    targetAmount = target_amount,
    currentAmount = current_amount,
    currency = currency,
    targetDate = target_date,
    remainingAmount = remaining_amount,
    requiredMonthlyContribution = required_monthly_contribution
)

fun ForecastDto.toDomain() = MonthEndForecast(currency = currency, forecastBalance = forecast_balance)

fun AvailableToSpendDto.toDomain() = AvailableToSpend(currency = currency, availableToSpend = available_to_spend)

fun DashboardDataDto.toDomain() = DashboardData(
    totalBalance = total_balance.map { it.toDomain() },
    thisMonth = this_month.map { it.toDomain() },
    budgets = budgets.map { it.toDomain() },
    goals = goals.map { it.toDomain() },
    forecast = forecast.map { it.toDomain() },
    availableToSpend = available_to_spend.map { it.toDomain() }
)
