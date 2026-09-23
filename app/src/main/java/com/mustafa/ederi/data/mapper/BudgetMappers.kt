package com.mustafa.ederi.data.mapper

import com.mustafa.ederi.data.remote.dto.BudgetDto
import com.mustafa.ederi.domain.model.Budget

fun BudgetDto.toDomain() = Budget(
    id = id,
    categoryId = category,
    amount = amount,
    currency = currency,
    startDate = start_date,
    endDate = end_date,
    spent = spent,
    remaining = remaining
)
