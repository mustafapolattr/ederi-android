package com.mustafa.ederi.data.repository

import com.mustafa.ederi.core.error.safeApiCall
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.mapper.toDomain
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.dto.GoalCreateRequestDto
import com.mustafa.ederi.domain.model.Goal
import com.mustafa.ederi.domain.model.GoalType
import com.mustafa.ederi.domain.repository.GoalRepository
import com.squareup.moshi.Moshi
import java.math.BigDecimal
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val moshi: Moshi
) : GoalRepository {

    override suspend fun getGoals(): NetworkResult<List<Goal>> =
        when (val result = safeApiCall(moshi) { apiService.getGoals() }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.results.map { it.toDomain() })
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }

    override suspend fun createGoal(
        name: String,
        goalType: GoalType,
        targetAmount: BigDecimal,
        currentAmount: BigDecimal,
        currency: String,
        targetDate: String
    ): NetworkResult<Goal> {
        val request = GoalCreateRequestDto(
            name = name,
            goal_type = goalType.name.lowercase(),
            target_amount = targetAmount,
            current_amount = currentAmount,
            currency = currency,
            target_date = targetDate
        )
        return when (val result = safeApiCall(moshi) { apiService.createGoal(request) }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.toDomain())
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }
}
