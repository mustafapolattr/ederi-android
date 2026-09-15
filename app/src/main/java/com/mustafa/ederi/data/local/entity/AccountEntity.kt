package com.mustafa.ederi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Money fields are stored as plain decimal strings — never Double — and parsed to BigDecimal in the mapper. */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val currency: String,
    val initialBalance: String,
    val currentBalance: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
