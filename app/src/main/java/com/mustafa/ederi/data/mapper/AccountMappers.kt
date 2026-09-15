package com.mustafa.ederi.data.mapper

import com.mustafa.ederi.data.local.entity.AccountEntity
import com.mustafa.ederi.data.remote.dto.AccountDto
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.model.AccountType
import java.math.BigDecimal

fun AccountDto.toEntity() = AccountEntity(
    id = id,
    name = name,
    type = type,
    currency = currency,
    initialBalance = initial_balance.toPlainString(),
    currentBalance = current_balance.toPlainString(),
    isActive = is_active,
    createdAt = created_at,
    updatedAt = updated_at
)

fun AccountEntity.toDomain() = Account(
    id = id,
    name = name,
    type = AccountType.valueOf(type.uppercase()),
    currency = currency,
    initialBalance = BigDecimal(initialBalance),
    currentBalance = BigDecimal(currentBalance),
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AccountDto.toDomain() = toEntity().toDomain()
