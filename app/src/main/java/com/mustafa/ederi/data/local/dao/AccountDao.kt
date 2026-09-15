package com.mustafa.ederi.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mustafa.ederi.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    /** Soft-deleted accounts stay in the table but are filtered out here. */
    @Query("SELECT * FROM accounts WHERE isActive = 1 ORDER BY name")
    fun observeActiveAccounts(): Flow<List<AccountEntity>>

    @Upsert
    suspend fun upsertAll(accounts: List<AccountEntity>)

    @Upsert
    suspend fun upsert(account: AccountEntity)
}
