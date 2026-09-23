package com.mustafa.ederi.domain.model

import java.math.BigDecimal

enum class GoalType { EMERGENCY_FUND, VACATION, CAR, HOME, EDUCATION, CUSTOM }

data class Goal(
    val id: String,
    val name: String,
    val goalType: GoalType,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val currency: String,
    val targetDate: String,
    val remainingAmount: BigDecimal,
    val requiredMonthlyContribution: BigDecimal
)
