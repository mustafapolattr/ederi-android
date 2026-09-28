package com.mustafa.ederi.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.DashboardData
import com.mustafa.ederi.domain.repository.AiRepository
import com.mustafa.ederi.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Content(val data: DashboardData) : HomeUiState()
    data class Error(val error: AppError) : HomeUiState()
}

sealed class InsightUiState {
    data object Idle : InsightUiState()
    data object Loading : InsightUiState()
    data class Content(val insight: String) : InsightUiState()
    data class Error(val error: AppError) : InsightUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _insightState = MutableStateFlow<InsightUiState>(InsightUiState.Idle)
    val insightState: StateFlow<InsightUiState> = _insightState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = dashboardRepository.getDashboard()) {
                is NetworkResult.Success -> HomeUiState.Content(result.data)
                is NetworkResult.Error -> HomeUiState.Error(result.error)
            }
        }
    }

    /** Only called from an explicit button tap — never automatically, to stay within the daily AI quota. */
    fun fetchInsight() {
        _insightState.value = InsightUiState.Loading
        viewModelScope.launch {
            _insightState.value = when (val result = aiRepository.analyze()) {
                is NetworkResult.Success -> InsightUiState.Content(result.data.insight)
                is NetworkResult.Error -> InsightUiState.Error(result.error)
            }
        }
    }
}
