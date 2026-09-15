package com.mustafa.ederi.data.mapper

import com.google.common.truth.Truth.assertThat
import com.mustafa.ederi.data.remote.dto.AccountDto
import com.mustafa.ederi.domain.model.AccountType
import org.junit.Test
import java.math.BigDecimal

class AccountMappersTest {

    private val dto = AccountDto(
        id = "acc-1",
        name = "Wallet",
        type = "credit_card",
        currency = "USD",
        initial_balance = BigDecimal("100.00"),
        current_balance = BigDecimal("87.50"),
        is_active = true,
        created_at = "2026-01-01T00:00:00Z",
        updated_at = "2026-01-02T00:00:00Z"
    )

    @Test
    fun `snake_case type string maps to the matching enum constant`() {
        assertThat(dto.toDomain().type).isEqualTo(AccountType.CREDIT_CARD)
    }

    @Test
    fun `dto to entity to domain round trip preserves balances exactly`() {
        val domain = dto.toEntity().toDomain()
        assertThat(domain.initialBalance).isEqualTo(BigDecimal("100.00"))
        assertThat(domain.currentBalance).isEqualTo(BigDecimal("87.50"))
    }

    @Test
    fun `dto to domain shortcut matches the entity round trip`() {
        assertThat(dto.toDomain()).isEqualTo(dto.toEntity().toDomain())
    }
}
