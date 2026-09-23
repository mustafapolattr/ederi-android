package com.mustafa.ederi.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.model.Category
import com.mustafa.ederi.domain.model.Transaction
import com.mustafa.ederi.domain.repository.AccountRepository
import com.mustafa.ederi.domain.repository.CategoryRepository
import com.mustafa.ederi.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionRow(
    val transaction: Transaction,
    val accountName: String,
    val categoryName: String?
)

sealed class TransactionsUiState {
    data object Loading : TransactionsUiState()
    data class Content(val rows: List<TransactionRow>, val isRefreshing: Boolean, val isOffline: Boolean) : TransactionsUiState()
    data object Empty : TransactionsUiState()
    data class Error(val error: AppError) : TransactionsUiState()
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val offlineBanner = MutableStateFlow(false)
    private val loadError = MutableStateFlow<AppError?>(null)

    val uiState: StateFlow<TransactionsUiState> = combine(
        transactionRepository.observeTransactions(),
        accountRepository.observeAccounts(),
        categoryRepository.observeCategories(),
        isRefreshing,
        offlineBanner,
        loadError
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val transactions = values[0] as List<Transaction>
        @Suppress("UNCHECKED_CAST")
        val accounts = values[1] as List<Account>
        @Suppress("UNCHECKED_CAST")
        val categories = values[2] as List<Category>
        val refreshing = values[3] as Boolean
        val offline = values[4] as Boolean
        val error = values[5] as AppError?

        val accountsById = accounts.associateBy { it.id }
        val categoriesById = categories.associateBy { it.id }
        val rows = transactions.map { tx ->
            TransactionRow(
                transaction = tx,
                accountName = accountsById[tx.accountId]?.name ?: tx.accountId,
                categoryName = tx.categoryId?.let { categoriesById[it]?.name }
            )
        }

        when {
            error != null && rows.isEmpty() -> TransactionsUiState.Error(error)
            rows.isEmpty() && !refreshing -> TransactionsUiState.Empty
            else -> TransactionsUiState.Content(rows, refreshing, offline)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionsUiState.Loading)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            val txResult = transactionRepository.refreshTransactions()
            val accountResult = accountRepository.refreshAccounts()
            val categoryResult = categoryRepository.refreshCategories()
            val errors = listOfNotNull(
                (txResult as? NetworkResult.Error)?.error,
                (accountResult as? NetworkResult.Error)?.error,
                (categoryResult as? NetworkResult.Error)?.error
            )
            when {
                errors.isEmpty() -> {
                    loadError.value = null
                    offlineBanner.value = false
                }
                errors.any { it is AppError.NoConnection } -> offlineBanner.value = true
                else -> loadError.value = errors.first()
            }
            isRefreshing.value = false
        }
    }
}
