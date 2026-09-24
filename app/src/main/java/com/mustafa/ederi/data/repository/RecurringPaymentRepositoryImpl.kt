package com.mustafa.ederi.data.repository

import com.mustafa.ederi.core.error.safeApiCall
import com.mustafa.ederi.core.error.safeUnitApiCall
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.mapper.toDomain
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.dto.RecurringPaymentCreateRequestDto
import com.mustafa.ederi.domain.model.RecurringPayment
import com.mustafa.ederi.domain.model.RecurringPaymentFrequency
import com.mustafa.ederi.domain.model.RecurringPaymentType
import com.mustafa.ederi.domain.repository.RecurringPaymentRepository
import com.squareup.moshi.Moshi
import java.math.BigDecimal
import javax.inject.Inject

class RecurringPaymentRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val moshi: Moshi
) : RecurringPaymentRepository {

    override suspend fun getRecurringPayments(): NetworkResult<List<RecurringPayment>> =
        when (val result = safeApiCall(moshi) { apiService.getRecurringPayments() }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.results.map { it.toDomain() })
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }

    override suspend fun createRecurringPayment(
        name: String,
        type: RecurringPaymentType,
        amount: BigDecimal,
        currency: String,
        frequency: RecurringPaymentFrequency,
        nextPaymentDate: String,
        categoryId: String?,
        accountId: String
    ): NetworkResult<RecurringPayment> {
        val request = RecurringPaymentCreateRequestDto(
            name = name,
            type = type.name.lowercase(),
            amount = amount,
            currency = currency,
            frequency = frequency.name.lowercase(),
            next_payment_date = nextPaymentDate,
            category = categoryId,
            account = accountId
        )
        return when (val result = safeApiCall(moshi) { apiService.createRecurringPayment(request) }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.toDomain())
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }

    override suspend fun deleteRecurringPayment(id: String): NetworkResult<Unit> =
        safeUnitApiCall(moshi) { apiService.deleteRecurringPayment(id) }
}
