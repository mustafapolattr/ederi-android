package com.mustafa.ederi.domain.repository

import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.DashboardData

interface DashboardRepository {
    suspend fun getDashboard(): NetworkResult<DashboardData>
}
