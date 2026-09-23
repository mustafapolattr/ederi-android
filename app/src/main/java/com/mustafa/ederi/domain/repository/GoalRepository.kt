package com.mustafa.ederi.domain.repository

import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Goal
import com.mustafa.ederi.domain.model.GoalType
import java.math.BigDecimal

interface GoalRepository {
    suspend fun getGoals(): NetworkResult<List<Goal>>

    suspend fun createGoal(
        name: String,
        goalType: GoalType,
        targetAmount: BigDecimal,
        currentAmount: BigDecimal,
        currency: String,
        targetDate: String
    ): NetworkResult<Goal>
}
