package com.mustafa.ederi.domain.model

import java.math.BigDecimal

enum class RecurringPaymentType { INCOME, EXPENSE }

enum class RecurringPaymentFrequency { WEEKLY, MONTHLY, QUARTERLY, YEARLY }

data class RecurringPayment(
    val id: String,
    val name: String,
    val type: RecurringPaymentType,
    val amount: BigDecimal,
    val currency: String,
    val frequency: RecurringPaymentFrequency,
    val nextPaymentDate: String,
    val categoryId: String?,
    val accountId: String,
    val isActive: Boolean
)
