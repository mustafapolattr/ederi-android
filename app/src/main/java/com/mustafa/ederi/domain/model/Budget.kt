package com.mustafa.ederi.domain.model

import java.math.BigDecimal

data class Budget(
    val id: String,
    val categoryId: String,
    val amount: BigDecimal,
    val currency: String,
    val startDate: String,
    val endDate: String,
    val spent: BigDecimal,
    val remaining: BigDecimal
)
