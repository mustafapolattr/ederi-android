package com.mustafa.ederi.data.repository

import com.mustafa.ederi.core.error.safeApiCall
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.local.dao.AccountDao
import com.mustafa.ederi.data.mapper.toDomain
import com.mustafa.ederi.data.mapper.toEntity
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.dto.AccountCreateRequestDto
import com.mustafa.ederi.data.remote.dto.AccountUpdateRequestDto
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.model.AccountType
import com.mustafa.ederi.domain.repository.AccountRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val accountDao: AccountDao,
    private val moshi: Moshi
) : AccountRepository {

    override fun observeAccounts(): Flow<List<Account>> =
        accountDao.observeActiveAccounts().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshAccounts(): NetworkResult<Unit> =
        when (val result = safeApiCall(moshi) { apiService.getAccounts() }) {
            is NetworkResult.Success -> {
                accountDao.upsertAll(result.data.results.map { it.toEntity() })
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }

    override suspend fun createAccount(
        name: String,
        type: AccountType,
        currency: String,
        initialBalance: BigDecimal
    ): NetworkResult<Account> {
        val request = AccountCreateRequestDto(
            name = name,
            type = type.name.lowercase(),
            currency = currency,
            initial_balance = initialBalance
        )
        return when (val result = safeApiCall(moshi) { apiService.createAccount(request) }) {
            is NetworkResult.Success -> {
                accountDao.upsert(result.data.toEntity())
                NetworkResult.Success(result.data.toDomain())
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }

    override suspend fun updateAccount(id: String, name: String?, type: AccountType?): NetworkResult<Account> {
        val request = AccountUpdateRequestDto(name = name, type = type?.name?.lowercase())
        return when (val result = safeApiCall(moshi) { apiService.updateAccount(id, request) }) {
            is NetworkResult.Success -> {
                accountDao.upsert(result.data.toEntity())
                NetworkResult.Success(result.data.toDomain())
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }

    override suspend fun deleteAccount(id: String): NetworkResult<Unit> =
        when (val result = safeApiCall(moshi) { apiService.deleteAccount(id) }) {
            is NetworkResult.Success -> {
                accountDao.upsert(result.data.toEntity())
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
}
