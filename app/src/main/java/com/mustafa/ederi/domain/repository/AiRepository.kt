package com.mustafa.ederi.domain.repository

import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.AnalyzeResult
import com.mustafa.ederi.domain.model.ChatResult
import com.mustafa.ederi.domain.model.ParseTransactionResult
import com.mustafa.ederi.domain.model.ScenarioResult
import java.math.BigDecimal

interface AiRepository {
    /** Never writes to the database — caller must confirm via [TransactionRepository.createTransaction] (spec §20). */
    suspend fun parseTransaction(text: String): NetworkResult<ParseTransactionResult>

    suspend fun analyze(): NetworkResult<AnalyzeResult>

    suspend fun chat(message: String, conversationId: String?): NetworkResult<ChatResult>

    suspend fun scenario(amount: BigDecimal, currency: String, description: String): NetworkResult<ScenarioResult>
}
