package com.mustafa.ederi.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.model.Category
import com.mustafa.ederi.domain.model.TransactionType
import com.mustafa.ederi.domain.repository.AccountRepository
import com.mustafa.ederi.domain.repository.CategoryRepository
import com.mustafa.ederi.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

sealed class AddTransactionUiState {
    data object Idle : AddTransactionUiState()
    data object Loading : AddTransactionUiState()
    data object Success : AddTransactionUiState()
    data class Error(val error: AppError) : AddTransactionUiState()
}

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val accounts: StateFlow<List<Account>> =
        accountRepository.observeAccounts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> =
        categoryRepository.observeCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle)
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.refreshAccounts()
            categoryRepository.refreshCategories()
        }
    }

    fun createTransaction(
        accountId: String,
        toAccountId: String?,
        categoryId: String?,
        type: TransactionType,
        amount: BigDecimal,
        currency: String,
        merchant: String?,
        description: String?,
        notes: String?,
        transactionDate: String
    ) {
        _uiState.value = AddTransactionUiState.Loading
        viewModelScope.launch {
            val result = transactionRepository.createTransaction(
                accountId = accountId,
                toAccountId = toAccountId,
                categoryId = categoryId,
                type = type,
                amount = amount,
                currency = currency,
                merchant = merchant,
                description = description,
                notes = notes,
                transactionDate = transactionDate
            )
            _uiState.value = when (result) {
                is NetworkResult.Success -> AddTransactionUiState.Success
                is NetworkResult.Error -> AddTransactionUiState.Error(result.error)
            }
        }
    }
}
