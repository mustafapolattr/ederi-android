package com.mustafa.ederi.domain.repository

import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Transaction
import com.mustafa.ederi.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface TransactionRepository {
    fun observeTransactions(
        accountId: String? = null,
        categoryId: String? = null,
        type: TransactionType? = null
    ): Flow<List<Transaction>>

    suspend fun refreshTransactions(): NetworkResult<Unit>

    suspend fun createTransaction(
        accountId: String,
        toAccountId: String?,
        categoryId: String?,
        type: TransactionType,
        amount: BigDecimal,
        currency: String,
        merchant: String?,
        description: String?,
        notes: String?,
        transactionDate: String
    ): NetworkResult<Transaction>

    suspend fun updateTransaction(
        id: String,
        categoryId: String?,
        merchant: String?,
        description: String?,
        notes: String?,
        transactionDate: String?
    ): NetworkResult<Transaction>

    suspend fun deleteTransaction(id: String): NetworkResult<Unit>
}
