package com.mustafa.ederi.data.mapper

import com.mustafa.ederi.data.remote.dto.RecurringPaymentDto
import com.mustafa.ederi.domain.model.RecurringPayment
import com.mustafa.ederi.domain.model.RecurringPaymentFrequency
import com.mustafa.ederi.domain.model.RecurringPaymentType

fun RecurringPaymentDto.toDomain() = RecurringPayment(
    id = id,
    name = name,
    type = RecurringPaymentType.valueOf(type.uppercase()),
    amount = amount,
    currency = currency,
    frequency = RecurringPaymentFrequency.valueOf(frequency.uppercase()),
    nextPaymentDate = next_payment_date,
    categoryId = category,
    accountId = account,
    isActive = is_active
)
