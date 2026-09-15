package com.mustafa.ederi.presentation.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AccountsUiState {
    data object Loading : AccountsUiState()
    data class Content(val accounts: List<Account>, val isRefreshing: Boolean, val isOffline: Boolean) : AccountsUiState()
    data object Empty : AccountsUiState()
    data class Error(val error: AppError) : AccountsUiState()
}

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val offlineBanner = MutableStateFlow(false)
    private val loadError = MutableStateFlow<AppError?>(null)

    val uiState: StateFlow<AccountsUiState> = combine(
        accountRepository.observeAccounts(),
        isRefreshing,
        offlineBanner,
        loadError
    ) { accounts, refreshing, offline, error ->
        when {
            error != null && accounts.isEmpty() -> AccountsUiState.Error(error)
            accounts.isEmpty() && !refreshing -> AccountsUiState.Empty
            else -> AccountsUiState.Content(accounts, refreshing, offline)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccountsUiState.Loading)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            when (val result = accountRepository.refreshAccounts()) {
                is NetworkResult.Success -> {
                    loadError.value = null
                    offlineBanner.value = false
                }
                is NetworkResult.Error -> {
                    if (result.error is AppError.NoConnection) {
                        offlineBanner.value = true
                    } else {
                        loadError.value = result.error
                    }
                }
            }
            isRefreshing.value = false
        }
    }
}
