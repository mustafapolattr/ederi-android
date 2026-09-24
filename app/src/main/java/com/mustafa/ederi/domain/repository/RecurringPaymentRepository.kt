package com.mustafa.ederi.domain.repository

import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.RecurringPayment
import com.mustafa.ederi.domain.model.RecurringPaymentFrequency
import com.mustafa.ederi.domain.model.RecurringPaymentType
import java.math.BigDecimal

interface RecurringPaymentRepository {
    suspend fun getRecurringPayments(): NetworkResult<List<RecurringPayment>>

    suspend fun createRecurringPayment(
        name: String,
        type: RecurringPaymentType,
        amount: BigDecimal,
        currency: String,
        frequency: RecurringPaymentFrequency,
        nextPaymentDate: String,
        categoryId: String?,
        accountId: String
    ): NetworkResult<RecurringPayment>

    suspend fun deleteRecurringPayment(id: String): NetworkResult<Unit>
}
