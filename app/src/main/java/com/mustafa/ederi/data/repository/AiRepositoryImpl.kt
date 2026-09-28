package com.mustafa.ederi.data.repository

import com.mustafa.ederi.core.error.safeApiCall
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.mapper.toDomain
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.dto.ChatRequestDto
import com.mustafa.ederi.data.remote.dto.ParseTransactionRequestDto
import com.mustafa.ederi.data.remote.dto.ScenarioRequestDto
import com.mustafa.ederi.domain.model.AnalyzeResult
import com.mustafa.ederi.domain.model.ChatResult
import com.mustafa.ederi.domain.model.ParseTransactionResult
import com.mustafa.ederi.domain.model.ScenarioResult
import com.mustafa.ederi.domain.repository.AiRepository
import com.squareup.moshi.Moshi
import java.math.BigDecimal
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val moshi: Moshi
) : AiRepository {

    override suspend fun parseTransaction(text: String): NetworkResult<ParseTransactionResult> =
        when (val result = safeApiCall(moshi) { apiService.parseTransaction(ParseTransactionRequestDto(text)) }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.data.toDomain())
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }

    override suspend fun analyze(): NetworkResult<AnalyzeResult> =
        when (val result = safeApiCall(moshi) { apiService.analyze() }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.data.toDomain())
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }

    override suspend fun chat(message: String, conversationId: String?): NetworkResult<ChatResult> =
        when (val result = safeApiCall(moshi) { apiService.chat(ChatRequestDto(message, conversationId)) }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.data.toDomain())
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }

    override suspend fun scenario(amount: BigDecimal, currency: String, description: String): NetworkResult<ScenarioResult> =
        when (val result = safeApiCall(moshi) { apiService.scenario(ScenarioRequestDto(amount, currency, description)) }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.data.toDomain())
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
}
