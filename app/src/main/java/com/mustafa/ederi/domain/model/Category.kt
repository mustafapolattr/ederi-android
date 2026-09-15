package com.mustafa.ederi.domain.model

enum class CategoryType { INCOME, EXPENSE, BOTH }

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: CategoryType,
    val isDefault: Boolean
)
