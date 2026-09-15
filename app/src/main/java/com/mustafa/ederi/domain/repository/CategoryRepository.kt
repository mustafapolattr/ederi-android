package com.mustafa.ederi.domain.repository

import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Category
import com.mustafa.ederi.domain.model.CategoryType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    /** Default (not editable) + the user's custom categories, local-first. */
    fun observeCategories(): Flow<List<Category>>

    suspend fun refreshCategories(): NetworkResult<Unit>

    suspend fun createCategory(name: String, icon: String, color: String, type: CategoryType): NetworkResult<Category>

    suspend fun updateCategory(
        id: String,
        name: String?,
        icon: String?,
        color: String?,
        type: CategoryType?
    ): NetworkResult<Category>

    suspend fun deleteCategory(id: String): NetworkResult<Unit>
}
