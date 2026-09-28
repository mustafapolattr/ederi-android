package com.mustafa.ederi.data.repository

import com.google.common.truth.Truth.assertThat
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.BigDecimalAdapter
import com.mustafa.ederi.data.remote.dto.AnalyzeDataDto
import com.mustafa.ederi.data.remote.dto.AnalyzeResponseDto
import com.mustafa.ederi.data.remote.dto.ChatDataDto
import com.mustafa.ederi.data.remote.dto.ChatResponseDto
import com.mustafa.ederi.data.remote.dto.ParseTransactionResponseDto
import com.mustafa.ederi.data.remote.dto.ParsedTransactionDataDto
import com.mustafa.ederi.data.remote.dto.ParsedTransactionDto
import com.mustafa.ederi.data.remote.dto.ScenarioDataDto
import com.mustafa.ederi.data.remote.dto.ScenarioResponseDto
import com.mustafa.ederi.domain.model.TransactionType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.Response
import java.math.BigDecimal

class AiRepositoryImplTest {

    private val moshi: Moshi = Moshi.Builder().add(BigDecimalAdapter()).add(KotlinJsonAdapterFactory()).build()
    private val apiService: ApiService = mockk()
    private val repository = AiRepositoryImpl(apiService, moshi)

    @Test
    fun `parseTransaction maps a resolved suggestion to domain`() = runTest {
        val dto = ParseTransactionResponseDto(
            success = true,
            data = ParsedTransactionDataDto(
                transaction = ParsedTransactionDto(
                    type = "expense",
                    amount = BigDecimal("42.50"),
                    currency = "USD",
                    account = "acc-1",
                    category = "cat-1",
                    merchant = "Starbucks",
                    description = "",
                    transaction_date = "2026-09-27"
                ),
                valid = true,
                errors = null,
                needs_clarification = null
            )
        )
        coEvery { apiService.parseTransaction(any()) } returns Response.success(dto)

        val result = repository.parseTransaction("Spent \$42.50 at Starbucks yesterday") as NetworkResult.Success

        assertThat(result.data.valid).isTrue()
        assertThat(result.data.transaction.type).isEqualTo(TransactionType.EXPENSE)
        assertThat(result.data.transaction.amount).isEqualTo(BigDecimal("42.50"))
        assertThat(result.data.transaction.accountId).isEqualTo("acc-1")
        assertThat(result.data.needsClarification).isNull()
    }

    @Test
    fun `parseTransaction surfaces needs_clarification when the account can't be resolved`() = runTest {
        val dto = ParseTransactionResponseDto(
            success = true,
            data = ParsedTransactionDataDto(
                transaction = ParsedTransactionDto(
                    type = "expense",
                    amount = BigDecimal("10.00"),
                    currency = null,
                    account = null,
                    category = null,
                    merchant = "",
                    description = "",
                    transaction_date = "2026-09-27"
                ),
                valid = false,
                errors = null,
                needs_clarification = "Which account did you use?"
            )
        )
        coEvery { apiService.parseTransaction(any()) } returns Response.success(dto)

        val result = repository.parseTransaction("Spent 10 dollars") as NetworkResult.Success

        assertThat(result.data.valid).isFalse()
        assertThat(result.data.transaction.accountId).isNull()
        assertThat(result.data.needsClarification).isEqualTo("Which account did you use?")
    }

    @Test
    fun `analyze maps the insight text to domain`() = runTest {
        coEvery { apiService.analyze(any()) } returns Response.success(
            AnalyzeResponseDto(success = true, data = AnalyzeDataDto(insight = "You spent 20% more on dining this month."))
        )

        val result = repository.analyze() as NetworkResult.Success

        assertThat(result.data.insight).isEqualTo("You spent 20% more on dining this month.")
    }

    @Test
    fun `chat maps conversation id and message to domain`() = runTest {
        coEvery { apiService.chat(any()) } returns Response.success(
            ChatResponseDto(success = true, data = ChatDataDto(conversation_id = "conv-1", message = "Sure, here's your summary."))
        )

        val result = repository.chat("How much did I spend?", null) as NetworkResult.Success

        assertThat(result.data.conversationId).isEqualTo("conv-1")
        assertThat(result.data.message).isEqualTo("Sure, here's your summary.")
    }

    @Test
    fun `scenario maps backend-computed projection to domain`() = runTest {
        coEvery { apiService.scenario(any()) } returns Response.success(
            ScenarioResponseDto(
                success = true,
                data = ScenarioDataDto(
                    currency = "USD",
                    current_available_to_spend = BigDecimal("500.00"),
                    hypothetical_amount = BigDecimal("100.00"),
                    projected_available_to_spend = BigDecimal("400.00"),
                    description = "New shoes",
                    explanation = "You'd still have a healthy buffer left this month."
                )
            )
        )

        val result = repository.scenario(BigDecimal("100.00"), "USD", "New shoes") as NetworkResult.Success

        assertThat(result.data.projectedAvailableToSpend).isEqualTo(BigDecimal("400.00"))
        assertThat(result.data.explanation).isEqualTo("You'd still have a healthy buffer left this month.")
    }
}
