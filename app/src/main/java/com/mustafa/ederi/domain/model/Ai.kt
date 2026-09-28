package com.mustafa.ederi.domain.model

import java.math.BigDecimal

/** The AI's raw suggestion for a transaction — never persisted directly (spec §20). */
data class ParsedTransaction(
    val type: TransactionType,
    val amount: BigDecimal,
    val currency: String?,
    val accountId: String?,
    val categoryId: String?,
    val merchant: String,
    val description: String,
    val transactionDate: String
)

data class ParseTransactionResult(
    val transaction: ParsedTransaction,
    /** True only if [transaction] already passed backend business validation (spec §20/§30). */
    val valid: Boolean,
    val errors: Map<String, List<String>>?,
    val needsClarification: String?
)

data class AnalyzeResult(val insight: String)

data class ChatResult(val conversationId: String, val message: String)

data class ScenarioResult(
    val currency: String,
    val currentAvailableToSpend: BigDecimal,
    val hypotheticalAmount: BigDecimal,
    val projectedAvailableToSpend: BigDecimal,
    val description: String,
    val explanation: String
)
