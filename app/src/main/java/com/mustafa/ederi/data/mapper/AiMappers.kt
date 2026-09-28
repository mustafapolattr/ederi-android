package com.mustafa.ederi.data.mapper

import com.mustafa.ederi.data.remote.dto.AnalyzeDataDto
import com.mustafa.ederi.data.remote.dto.ChatDataDto
import com.mustafa.ederi.data.remote.dto.ParsedTransactionDataDto
import com.mustafa.ederi.data.remote.dto.ParsedTransactionDto
import com.mustafa.ederi.data.remote.dto.ScenarioDataDto
import com.mustafa.ederi.domain.model.AnalyzeResult
import com.mustafa.ederi.domain.model.ChatResult
import com.mustafa.ederi.domain.model.ParseTransactionResult
import com.mustafa.ederi.domain.model.ParsedTransaction
import com.mustafa.ederi.domain.model.ScenarioResult
import com.mustafa.ederi.domain.model.TransactionType

fun ParsedTransactionDto.toDomain() = ParsedTransaction(
    type = TransactionType.valueOf(type.uppercase()),
    amount = amount,
    currency = currency,
    accountId = account,
    categoryId = category,
    merchant = merchant,
    description = description,
    transactionDate = transaction_date
)

fun ParsedTransactionDataDto.toDomain() = ParseTransactionResult(
    transaction = transaction.toDomain(),
    valid = valid,
    errors = errors,
    needsClarification = needs_clarification
)

fun AnalyzeDataDto.toDomain() = AnalyzeResult(insight = insight)

fun ChatDataDto.toDomain() = ChatResult(conversationId = conversation_id, message = message)

fun ScenarioDataDto.toDomain() = ScenarioResult(
    currency = currency,
    currentAvailableToSpend = current_available_to_spend,
    hypotheticalAmount = hypothetical_amount,
    projectedAvailableToSpend = projected_available_to_spend,
    description = description,
    explanation = explanation
)
