package com.mustafa.ederi.data.mapper

import com.google.common.truth.Truth.assertThat
import com.mustafa.ederi.data.remote.dto.TransactionDto
import com.mustafa.ederi.domain.model.TransactionType
import org.junit.Test
import java.math.BigDecimal

class TransactionMappersTest {

    private val dto = TransactionDto(
        id = "tx-1",
        account = "acc-1",
        to_account = "acc-2",
        category = "cat-1",
        type = "transfer",
        amount = BigDecimal("250.00"),
        currency = "USD",
        merchant = null,
        description = "Move to savings",
        notes = null,
        transaction_date = "2026-01-05",
        created_at = "2026-01-05T10:00:00Z",
        updated_at = "2026-01-05T10:00:00Z"
    )

    @Test
    fun `type string maps to the matching enum constant`() {
        assertThat(dto.toDomain().type).isEqualTo(TransactionType.TRANSFER)
    }

    @Test
    fun `dto to entity to domain round trip preserves amount exactly`() {
        assertThat(dto.toEntity().toDomain().amount).isEqualTo(BigDecimal("250.00"))
    }

    @Test
    fun `to_account_id survives the round trip for transfers`() {
        assertThat(dto.toEntity().toDomain().toAccountId).isEqualTo("acc-2")
    }

    @Test
    fun `dto to domain shortcut matches the entity round trip`() {
        assertThat(dto.toDomain()).isEqualTo(dto.toEntity().toDomain())
    }
}
