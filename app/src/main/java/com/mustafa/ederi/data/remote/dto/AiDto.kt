package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class ParseTransactionRequestDto(val text: String)

@JsonClass(generateAdapter = true)
data class ParseTransactionResponseDto(
    val success: Boolean,
    val data: ParsedTransactionDataDto
)

@JsonClass(generateAdapter = true)
data class ParsedTransactionDataDto(
    val transaction: ParsedTransactionDto,
    val valid: Boolean,
    val errors: Map<String, List<String>>?,
    val needs_clarification: String?
)

@JsonClass(generateAdapter = true)
data class ParsedTransactionDto(
    val type: String,
    val amount: BigDecimal,
    val currency: String?,
    val account: String?,
    val category: String?,
    val merchant: String,
    val description: String,
    val transaction_date: String
)

@JsonClass(generateAdapter = true)
data class AnalyzeResponseDto(
    val success: Boolean,
    val data: AnalyzeDataDto
)

@JsonClass(generateAdapter = true)
data class AnalyzeDataDto(val insight: String)

@JsonClass(generateAdapter = true)
data class ChatRequestDto(val message: String, val conversation_id: String? = null)

@JsonClass(generateAdapter = true)
data class ChatResponseDto(
    val success: Boolean,
    val data: ChatDataDto
)

@JsonClass(generateAdapter = true)
data class ChatDataDto(val conversation_id: String, val message: String)

@JsonClass(generateAdapter = true)
data class ScenarioRequestDto(val amount: BigDecimal, val currency: String, val description: String? = null)

@JsonClass(generateAdapter = true)
data class ScenarioResponseDto(
    val success: Boolean,
    val data: ScenarioDataDto
)

@JsonClass(generateAdapter = true)
data class ScenarioDataDto(
    val currency: String,
    val current_available_to_spend: BigDecimal,
    val hypothetical_amount: BigDecimal,
    val projected_available_to_spend: BigDecimal,
    val description: String,
    val explanation: String
)
