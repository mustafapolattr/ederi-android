package com.mustafa.ederi.presentation.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Goal
import com.mustafa.ederi.domain.model.GoalType
import com.mustafa.ederi.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

sealed class GoalsUiState {
    data object Loading : GoalsUiState()
    data class Content(val goals: List<Goal>) : GoalsUiState()
    data object Empty : GoalsUiState()
    data class Error(val error: AppError) : GoalsUiState()
}

sealed class CreateGoalUiState {
    data object Idle : CreateGoalUiState()
    data object Loading : CreateGoalUiState()
    data object Success : CreateGoalUiState()
    data class Error(val error: AppError) : CreateGoalUiState()
}

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GoalsUiState>(GoalsUiState.Loading)
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    private val _createState = MutableStateFlow<CreateGoalUiState>(CreateGoalUiState.Idle)
    val createState: StateFlow<CreateGoalUiState> = _createState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = GoalsUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = goalRepository.getGoals()) {
                is NetworkResult.Success ->
                    if (result.data.isEmpty()) GoalsUiState.Empty else GoalsUiState.Content(result.data)
                is NetworkResult.Error -> GoalsUiState.Error(result.error)
            }
        }
    }

    fun createGoal(
        name: String,
        goalType: GoalType,
        targetAmount: BigDecimal,
        currentAmount: BigDecimal,
        currency: String,
        targetDate: String
    ) {
        _createState.value = CreateGoalUiState.Loading
        viewModelScope.launch {
            val result = goalRepository.createGoal(name, goalType, targetAmount, currentAmount, currency, targetDate)
            _createState.value = when (result) {
                is NetworkResult.Success -> {
                    refresh()
                    CreateGoalUiState.Success
                }
                is NetworkResult.Error -> CreateGoalUiState.Error(result.error)
            }
        }
    }

    fun resetCreateState() {
        _createState.value = CreateGoalUiState.Idle
    }
}
