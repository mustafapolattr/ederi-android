package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass

/** Shape used by every list endpoint (accounts/categories/transactions): {success, count, next, previous, results}. */
@JsonClass(generateAdapter = true)
data class PaginatedResponseDto<T>(
    val success: Boolean,
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)
