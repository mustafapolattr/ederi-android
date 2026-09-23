package com.mustafa.ederi.domain.model

import java.math.BigDecimal

data class CurrencyAmount(val currency: String, val amount: BigDecimal)

data class MonthSummary(val currency: String, val income: BigDecimal, val expense: BigDecimal, val saved: BigDecimal)

/** `category` is a display name here, unlike [Budget.categoryId] (see apps/core/services.py on the backend). */
data class DashboardBudget(
    val id: String,
    val category: String,
    val amount: BigDecimal,
    val currency: String,
    val spent: BigDecimal,
    val remaining: BigDecimal
)

data class DashboardGoal(
    val id: String,
    val name: String,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val currency: String,
    val targetDate: String,
    val remainingAmount: BigDecimal,
    val requiredMonthlyContribution: BigDecimal
)

data class DashboardData(
    val totalBalance: List<CurrencyAmount>,
    val thisMonth: List<MonthSummary>,
    val budgets: List<DashboardBudget>,
    val goals: List<DashboardGoal>
)
