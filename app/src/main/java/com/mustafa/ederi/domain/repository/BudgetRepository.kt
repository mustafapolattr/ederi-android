package com.mustafa.ederi.domain.repository

import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Budget
import java.math.BigDecimal

interface BudgetRepository {
    suspend fun getBudgets(): NetworkResult<List<Budget>>

    suspend fun createBudget(
        categoryId: String,
        amount: BigDecimal,
        currency: String,
        startDate: String,
        endDate: String
    ): NetworkResult<Budget>
}
