package com.mustafa.ederi.presentation.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Budget
import com.mustafa.ederi.domain.model.Category
import com.mustafa.ederi.domain.repository.BudgetRepository
import com.mustafa.ederi.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class BudgetRow(val budget: Budget, val categoryName: String)

sealed class BudgetsUiState {
    data object Loading : BudgetsUiState()
    data class Content(val rows: List<BudgetRow>) : BudgetsUiState()
    data object Empty : BudgetsUiState()
    data class Error(val error: AppError) : BudgetsUiState()
}

sealed class CreateBudgetUiState {
    data object Idle : CreateBudgetUiState()
    data object Loading : CreateBudgetUiState()
    data object Success : CreateBudgetUiState()
    data class Error(val error: AppError) : CreateBudgetUiState()
}

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: StateFlow<List<Category>> =
        categoryRepository.observeCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val budgets = MutableStateFlow<List<Budget>?>(null)
    private val loadError = MutableStateFlow<AppError?>(null)

    val uiState: StateFlow<BudgetsUiState> = combine(budgets, categories, loadError) { budgetList, categoryList, error ->
        val categoriesById = categoryList.associateBy { it.id }
        when {
            budgetList == null -> if (error != null) BudgetsUiState.Error(error) else BudgetsUiState.Loading
            budgetList.isEmpty() -> BudgetsUiState.Empty
            else -> BudgetsUiState.Content(
                budgetList.map { BudgetRow(it, categoriesById[it.categoryId]?.name ?: it.categoryId) }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetsUiState.Loading)

    private val _createState = MutableStateFlow<CreateBudgetUiState>(CreateBudgetUiState.Idle)
    val createState: StateFlow<CreateBudgetUiState> = _createState.asStateFlow()

    init {
        viewModelScope.launch { categoryRepository.refreshCategories() }
        refresh()
    }

    fun refresh() {
        loadError.value = null
        viewModelScope.launch {
            when (val result = budgetRepository.getBudgets()) {
                is NetworkResult.Success -> budgets.value = result.data
                is NetworkResult.Error -> loadError.value = result.error
            }
        }
    }

    fun createBudget(categoryId: String, amount: BigDecimal, currency: String, startDate: String, endDate: String) {
        _createState.value = CreateBudgetUiState.Loading
        viewModelScope.launch {
            when (val result = budgetRepository.createBudget(categoryId, amount, currency, startDate, endDate)) {
                is NetworkResult.Success -> {
                    _createState.value = CreateBudgetUiState.Success
                    refresh()
                }
                is NetworkResult.Error -> _createState.value = CreateBudgetUiState.Error(result.error)
            }
        }
    }

    fun resetCreateState() {
        _createState.value = CreateBudgetUiState.Idle
    }
}
