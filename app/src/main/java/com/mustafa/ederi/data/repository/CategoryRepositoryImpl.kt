package com.mustafa.ederi.data.repository

import com.mustafa.ederi.core.error.safeApiCall
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.local.dao.CategoryDao
import com.mustafa.ederi.data.mapper.toDomain
import com.mustafa.ederi.data.mapper.toEntity
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.dto.CategoryCreateRequestDto
import com.mustafa.ederi.data.remote.dto.CategoryUpdateRequestDto
import com.mustafa.ederi.domain.model.Category
import com.mustafa.ederi.domain.model.CategoryType
import com.mustafa.ederi.domain.repository.CategoryRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val categoryDao: CategoryDao,
    private val moshi: Moshi
) : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeCategories().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshCategories(): NetworkResult<Unit> =
        when (val result = safeApiCall(moshi) { apiService.getCategories() }) {
            is NetworkResult.Success -> {
                categoryDao.upsertAll(result.data.results.map { it.toEntity() })
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }

    override suspend fun createCategory(name: String, icon: String, color: String, type: CategoryType): NetworkResult<Category> {
        val request = CategoryCreateRequestDto(name = name, icon = icon, color = color, type = type.name.lowercase())
        return when (val result = safeApiCall(moshi) { apiService.createCategory(request) }) {
            is NetworkResult.Success -> {
                categoryDao.upsert(result.data.toEntity())
                NetworkResult.Success(result.data.toDomain())
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }

    override suspend fun updateCategory(
        id: String,
        name: String?,
        icon: String?,
        color: String?,
        type: CategoryType?
    ): NetworkResult<Category> {
        val request = CategoryUpdateRequestDto(name = name, icon = icon, color = color, type = type?.name?.lowercase())
        return when (val result = safeApiCall(moshi) { apiService.updateCategory(id, request) }) {
            is NetworkResult.Success -> {
                categoryDao.upsert(result.data.toEntity())
                NetworkResult.Success(result.data.toDomain())
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
    }

    override suspend fun deleteCategory(id: String): NetworkResult<Unit> =
        when (val result = safeApiCall(moshi) { apiService.deleteCategory(id) }) {
            is NetworkResult.Success -> {
                categoryDao.delete(id)
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
}
