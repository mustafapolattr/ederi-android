package com.mustafa.ederi.domain.model

import java.math.BigDecimal

enum class TransactionType { INCOME, EXPENSE, TRANSFER, REFUND, ADJUSTMENT }

/**
 * [toAccountId] is required only for [TransactionType.TRANSFER] and must
 * share [currency] with both accounts — enforced by the backend, not
 * recomputed here.
 */
data class Transaction(
    val id: String,
    val accountId: String,
    val toAccountId: String?,
    val categoryId: String?,
    val type: TransactionType,
    val amount: BigDecimal,
    val currency: String,
    val merchant: String?,
    val description: String?,
    val notes: String?,
    val transactionDate: String,
    val createdAt: String,
    val updatedAt: String
)
