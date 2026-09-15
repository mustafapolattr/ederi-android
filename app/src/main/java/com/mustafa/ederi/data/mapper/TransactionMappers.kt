package com.mustafa.ederi.data.mapper

import com.mustafa.ederi.data.local.entity.TransactionEntity
import com.mustafa.ederi.data.remote.dto.TransactionDto
import com.mustafa.ederi.domain.model.Transaction
import com.mustafa.ederi.domain.model.TransactionType
import java.math.BigDecimal

fun TransactionDto.toEntity() = TransactionEntity(
    id = id,
    accountId = account_id,
    toAccountId = to_account_id,
    categoryId = category_id,
    type = type,
    amount = amount.toPlainString(),
    currency = currency,
    merchant = merchant,
    description = description,
    notes = notes,
    transactionDate = transaction_date,
    createdAt = created_at,
    updatedAt = updated_at
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    accountId = accountId,
    toAccountId = toAccountId,
    categoryId = categoryId,
    type = TransactionType.valueOf(type.uppercase()),
    amount = BigDecimal(amount),
    currency = currency,
    merchant = merchant,
    description = description,
    notes = notes,
    transactionDate = transactionDate,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun TransactionDto.toDomain() = toEntity().toDomain()
