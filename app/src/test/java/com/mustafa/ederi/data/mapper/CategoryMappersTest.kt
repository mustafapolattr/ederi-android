package com.mustafa.ederi.data.mapper

import com.google.common.truth.Truth.assertThat
import com.mustafa.ederi.data.remote.dto.CategoryDto
import com.mustafa.ederi.domain.model.CategoryType
import org.junit.Test

class CategoryMappersTest {

    private val dto = CategoryDto(
        id = "cat-1",
        name = "Food",
        icon = "restaurant",
        color = "#FF0000",
        type = "expense",
        is_default = true
    )

    @Test
    fun `type string maps to the matching enum constant`() {
        assertThat(dto.toDomain().type).isEqualTo(CategoryType.EXPENSE)
    }

    @Test
    fun `is_default is preserved through entity round trip`() {
        assertThat(dto.toEntity().toDomain().isDefault).isTrue()
    }

    @Test
    fun `dto to domain shortcut matches the entity round trip`() {
        assertThat(dto.toDomain()).isEqualTo(dto.toEntity().toDomain())
    }
}
