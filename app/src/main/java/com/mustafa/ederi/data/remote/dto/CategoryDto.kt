package com.mustafa.ederi.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryDto(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: String,
    val is_default: Boolean
)

@JsonClass(generateAdapter = true)
data class CategoryCreateRequestDto(
    val name: String,
    val icon: String,
    val color: String,
    val type: String
)

@JsonClass(generateAdapter = true)
data class CategoryUpdateRequestDto(
    val name: String? = null,
    val icon: String? = null,
    val color: String? = null,
    val type: String? = null
)
