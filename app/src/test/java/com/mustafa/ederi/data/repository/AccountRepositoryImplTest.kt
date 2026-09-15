package com.mustafa.ederi.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.local.dao.AccountDao
import com.mustafa.ederi.data.local.entity.AccountEntity
import com.mustafa.ederi.data.mapper.toEntity
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.BigDecimalAdapter
import com.mustafa.ederi.data.remote.dto.AccountDto
import com.mustafa.ederi.data.remote.dto.PaginatedResponseDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.Response
import java.math.BigDecimal

class AccountRepositoryImplTest {

    private val moshi: Moshi = Moshi.Builder().add(BigDecimalAdapter()).add(KotlinJsonAdapterFactory()).build()
    private val apiService: ApiService = mockk()
    private val accountDao: AccountDao = mockk(relaxed = true)
    private val repository = AccountRepositoryImpl(apiService, accountDao, moshi)

    @Test
    fun `observeAccounts maps cached entities to domain accounts`() = runTest {
        val entity = AccountEntity(
            id = "acc-1", name = "Wallet", type = "cash", currency = "USD",
            initialBalance = "10.00", currentBalance = "5.00", isActive = true,
            createdAt = "2026-01-01", updatedAt = "2026-01-01"
        )
        every { accountDao.observeActiveAccounts() } returns flowOf(listOf(entity))

        repository.observeAccounts().test {
            val accounts = awaitItem()
            assertThat(accounts).hasSize(1)
            assertThat(accounts.first().currentBalance).isEqualTo(BigDecimal("5.00"))
            awaitComplete()
        }
    }

    @Test
    fun `refreshAccounts upserts the results page into the cache on success`() = runTest {
        val dto = AccountDto(
            id = "acc-1", name = "Wallet", type = "cash", currency = "USD",
            initial_balance = BigDecimal("10.00"), current_balance = BigDecimal("5.00"),
            is_active = true, created_at = "2026-01-01", updated_at = "2026-01-01"
        )
        coEvery { apiService.getAccounts(any()) } returns Response.success(
            PaginatedResponseDto(success = true, count = 1, next = null, previous = null, results = listOf(dto))
        )

        val result = repository.refreshAccounts()

        assertThat(result).isEqualTo(NetworkResult.Success(Unit))
        coVerify { accountDao.upsertAll(listOf(dto.toEntity())) }
    }
}
