package com.mustafa.ederi.presentation.screens.recurringpayments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.model.Category
import com.mustafa.ederi.domain.model.RecurringPayment
import com.mustafa.ederi.domain.model.RecurringPaymentFrequency
import com.mustafa.ederi.domain.model.RecurringPaymentType
import com.mustafa.ederi.domain.repository.AccountRepository
import com.mustafa.ederi.domain.repository.CategoryRepository
import com.mustafa.ederi.domain.repository.RecurringPaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

sealed class RecurringPaymentsUiState {
    data object Loading : RecurringPaymentsUiState()
    data class Content(val payments: List<RecurringPayment>) : RecurringPaymentsUiState()
    data object Empty : RecurringPaymentsUiState()
    data class Error(val error: AppError) : RecurringPaymentsUiState()
}

sealed class CreateRecurringPaymentUiState {
    data object Idle : CreateRecurringPaymentUiState()
    data object Loading : CreateRecurringPaymentUiState()
    data object Success : CreateRecurringPaymentUiState()
    data class Error(val error: AppError) : CreateRecurringPaymentUiState()
}

@HiltViewModel
class RecurringPaymentsViewModel @Inject constructor(
    private val recurringPaymentRepository: RecurringPaymentRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val accounts: StateFlow<List<Account>> =
        accountRepository.observeAccounts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> =
        categoryRepository.observeCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<RecurringPaymentsUiState>(RecurringPaymentsUiState.Loading)
    val uiState: StateFlow<RecurringPaymentsUiState> = _uiState.asStateFlow()

    private val _createState = MutableStateFlow<CreateRecurringPaymentUiState>(CreateRecurringPaymentUiState.Idle)
    val createState: StateFlow<CreateRecurringPaymentUiState> = _createState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.refreshAccounts()
            categoryRepository.refreshCategories()
        }
        refresh()
    }

    fun refresh() {
        _uiState.value = RecurringPaymentsUiState.Loading
        viewModelScope.launch {
            when (val result = recurringPaymentRepository.getRecurringPayments()) {
                is NetworkResult.Success -> {
                    _uiState.value = if (result.data.isEmpty()) {
                        RecurringPaymentsUiState.Empty
                    } else {
                        RecurringPaymentsUiState.Content(result.data)
                    }
                }
                is NetworkResult.Error -> _uiState.value = RecurringPaymentsUiState.Error(result.error)
            }
        }
    }

    fun createRecurringPayment(
        name: String,
        type: RecurringPaymentType,
        amount: BigDecimal,
        currency: String,
        frequency: RecurringPaymentFrequency,
        nextPaymentDate: String,
        categoryId: String?,
        accountId: String
    ) {
        _createState.value = CreateRecurringPaymentUiState.Loading
        viewModelScope.launch {
            val result = recurringPaymentRepository.createRecurringPayment(
                name = name,
                type = type,
                amount = amount,
                currency = currency,
                frequency = frequency,
                nextPaymentDate = nextPaymentDate,
                categoryId = categoryId,
                accountId = accountId
            )
            _createState.value = when (result) {
                is NetworkResult.Success -> {
                    refresh()
                    CreateRecurringPaymentUiState.Success
                }
                is NetworkResult.Error -> CreateRecurringPaymentUiState.Error(result.error)
            }
        }
    }

    fun resetCreateState() {
        _createState.value = CreateRecurringPaymentUiState.Idle
    }

    fun deleteRecurringPayment(id: String) {
        viewModelScope.launch {
            when (recurringPaymentRepository.deleteRecurringPayment(id)) {
                is NetworkResult.Success -> refresh()
                is NetworkResult.Error -> Unit
            }
        }
    }
}
