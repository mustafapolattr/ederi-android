package com.mustafa.ederi.domain.repository

import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.model.AccountType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface AccountRepository {
    /** Local-first, active accounts only (soft-deleted rows stay cached but hidden). */
    fun observeAccounts(): Flow<List<Account>>

    suspend fun refreshAccounts(): NetworkResult<Unit>

    suspend fun createAccount(
        name: String,
        type: AccountType,
        currency: String,
        initialBalance: BigDecimal
    ): NetworkResult<Account>

    /** [name]/[type] only — currency and initial balance are immutable after creation. */
    suspend fun updateAccount(id: String, name: String?, type: AccountType?): NetworkResult<Account>

    /** Soft delete — the backend returns the account with is_active=false, it is not removed locally. */
    suspend fun deleteAccount(id: String): NetworkResult<Unit>
}
