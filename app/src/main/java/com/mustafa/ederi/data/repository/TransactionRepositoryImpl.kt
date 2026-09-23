package com.mustafa.ederi.data.repository

import com.mustafa.ederi.core.error.safeApiCall
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.local.dao.TransactionDao
import com.mustafa.ederi.data.mapper.toDomain
import com.mustafa.ederi.data.mapper.toEntity
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.dto.TransactionCreateRequestDto
import com.mustafa.ederi.data.remote.dto.TransactionUpdateRequestDto
import com.mustafa.ederi.domain.model.Transaction
import com.mustafa.ederi.domain.model.TransactionType
import com.mustafa.ederi.domain.repository.TransactionRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val transactionDao: TransactionDao,
    private val moshi: Moshi
) : TransactionRepository {

    override fun observeTransactions(
        accountId: String?,
        categoryId: String?,
        type: TransactionType?
    ): Flow<List<Transaction>> =
        transactionDao.observeTransactions(accountId, categoryId, type?.name?.lowercase())
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshTransactions(): NetworkResult<Unit> =
        when (val result = safeApiCall(moshi) { apiService.getTransactions() }) {
            is NetworkResult.Success -> {
                transactionDao.upsertAll(result.data.results.map { it.toEntity() })
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }

    override suspend fun createTransaction(
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
    ): NetworkResult<Transaction> {
        val request = TransactionCreateRequestDto(
            account = accountId,
            to_account = toAccountId,
            category = categoryId,
            type = type.name.lowercase(),
            amount = amount,
            currency = currency,
            merchant = merchant,
            description = description,
            notes = notes,
            transaction_date = transactionDate
        )
        return when (val result = safeApiCall(moshi) { apiService.createTransaction(request) }) {
            is NetworkResult.Success -> {
                transactionDao.upsert(result.data.toEntity())
                NetworkResult.Success(result.data.toDomain())
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }

    override suspend fun updateTransaction(
        id: String,
        categoryId: String?,
        merchant: String?,
        description: String?,
        notes: String?,
        transactionDate: String?
    ): NetworkResult<Transaction> {
        val request = TransactionUpdateRequestDto(
            category = categoryId,
            merchant = merchant,
            description = description,
            notes = notes,
            transaction_date = transactionDate
        )
        return when (val result = safeApiCall(moshi) { apiService.updateTransaction(id, request) }) {
            is NetworkResult.Success -> {
                transactionDao.upsert(result.data.toEntity())
                NetworkResult.Success(result.data.toDomain())
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }

    override suspend fun deleteTransaction(id: String): NetworkResult<Unit> =
        when (val result = safeApiCall(moshi) { apiService.deleteTransaction(id) }) {
            is NetworkResult.Success -> {
                transactionDao.delete(id)
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
}
