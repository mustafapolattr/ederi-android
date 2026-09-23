package com.mustafa.ederi.data.repository

import com.mustafa.ederi.core.error.safeApiCall
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.data.mapper.toDomain
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.domain.model.DashboardData
import com.mustafa.ederi.domain.repository.DashboardRepository
import com.squareup.moshi.Moshi
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val moshi: Moshi
) : DashboardRepository {

    override suspend fun getDashboard(): NetworkResult<DashboardData> =
        when (val result = safeApiCall(moshi) { apiService.getDashboard() }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.data.toDomain())
            is NetworkResult.Error -> NetworkResult.Error(result.error)
        }
}
