package com.mustafa.ederi.domain.model

import java.math.BigDecimal

enum class AccountType { CASH, BANK, CREDIT_CARD, SAVINGS, INVESTMENT, OTHER }

/**
 * [currentBalance] is always backend-computed (spec §15) — the client only
 * ever displays it, never derives or recalculates it.
 * [initialBalance] and [currency] are immutable after creation.
 */
data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val currency: String,
    val initialBalance: BigDecimal,
    val currentBalance: BigDecimal,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
