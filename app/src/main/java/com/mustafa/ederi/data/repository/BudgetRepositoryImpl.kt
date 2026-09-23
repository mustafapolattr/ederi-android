package com.mustafa.ederi.data.repository

import com.mustafa.ederi.core.error.safeApiCall
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.mapper.toDomain
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.dto.BudgetCreateRequestDto
import com.mustafa.ederi.domain.model.Budget
import com.mustafa.ederi.domain.repository.BudgetRepository
import com.squareup.moshi.Moshi
import java.math.BigDecimal
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val moshi: Moshi
) : BudgetRepository {

    override suspend fun getBudgets(): NetworkResult<List<Budget>> =
        when (val result = safeApiCall(moshi) { apiService.getBudgets() }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.results.map { it.toDomain() })
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }

    override suspend fun createBudget(
        categoryId: String,
        amount: BigDecimal,
        currency: String,
        startDate: String,
        endDate: String
    ): NetworkResult<Budget> {
        val request = BudgetCreateRequestDto(
            category = categoryId,
            amount = amount,
            currency = currency,
            start_date = startDate,
            end_date = endDate
        )
        return when (val result = safeApiCall(moshi) { apiService.createBudget(request) }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.toDomain())
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }
}
