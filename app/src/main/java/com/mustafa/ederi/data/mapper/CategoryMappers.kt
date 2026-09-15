package com.mustafa.ederi.data.mapper

import com.mustafa.ederi.data.local.entity.CategoryEntity
import com.mustafa.ederi.data.remote.dto.CategoryDto
import com.mustafa.ederi.domain.model.Category
import com.mustafa.ederi.domain.model.CategoryType

fun CategoryDto.toEntity() = CategoryEntity(
    id = id,
    name = name,
    icon = icon,
    color = color,
    type = type,
    isDefault = is_default
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    icon = icon,
    color = color,
    type = CategoryType.valueOf(type.uppercase()),
    isDefault = isDefault
)

fun CategoryDto.toDomain() = toEntity().toDomain()
