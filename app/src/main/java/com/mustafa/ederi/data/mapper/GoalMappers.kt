package com.mustafa.ederi.data.mapper

import com.mustafa.ederi.data.remote.dto.GoalDto
import com.mustafa.ederi.domain.model.Goal
import com.mustafa.ederi.domain.model.GoalType

fun GoalDto.toDomain() = Goal(
    id = id,
    name = name,
    goalType = GoalType.valueOf(goal_type.uppercase()),
    targetAmount = target_amount,
    currentAmount = current_amount,
    currency = currency,
    targetDate = target_date,
    remainingAmount = remaining_amount,
    requiredMonthlyContribution = required_monthly_contribution
)
