package com.mustafa.ederi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** [amount] is stored as a plain decimal string — never Double — and parsed to BigDecimal in the mapper. */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val accountId: String,
    val toAccountId: String?,
    val categoryId: String?,
    val type: String,
    val amount: String,
    val currency: String,
    val merchant: String?,
    val description: String?,
    val notes: String?,
    val transactionDate: String,
    val createdAt: String,
    val updatedAt: String
)
