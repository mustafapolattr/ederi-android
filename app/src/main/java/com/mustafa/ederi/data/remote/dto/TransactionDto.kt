package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class TransactionDto(
    val id: String,
    val account_id: String,
    val to_account_id: String?,
    val category_id: String?,
    val type: String,
    val amount: BigDecimal,
    val currency: String,
    val merchant: String?,
    val description: String?,
    val notes: String?,
    val transaction_date: String,
    val created_at: String,
    val updated_at: String
)

@JsonClass(generateAdapter = true)
data class TransactionCreateRequestDto(
    val account_id: String,
    val to_account_id: String? = null,
    val category_id: String? = null,
    val type: String,
    val amount: BigDecimal,
    val currency: String,
    val merchant: String? = null,
    val description: String? = null,
    val notes: String? = null,
    val transaction_date: String
)

@JsonClass(generateAdapter = true)
data class TransactionUpdateRequestDto(
    val category_id: String? = null,
    val merchant: String? = null,
    val description: String? = null,
    val notes: String? = null,
    val transaction_date: String? = null
)
