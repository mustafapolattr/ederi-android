package com.mustafa.ederi.presentation.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.AccountType
import com.mustafa.ederi.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

sealed class AddAccountUiState {
    data object Idle : AddAccountUiState()
    data object Loading : AddAccountUiState()
    data object Success : AddAccountUiState()
    data class Error(val error: AppError) : AddAccountUiState()
}

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAccountUiState>(AddAccountUiState.Idle)
    val uiState: StateFlow<AddAccountUiState> = _uiState.asStateFlow()

    fun createAccount(name: String, type: AccountType, currency: String, initialBalance: BigDecimal) {
        _uiState.value = AddAccountUiState.Loading
        viewModelScope.launch {
            when (val result = accountRepository.createAccount(name, type, currency, initialBalance)) {
                is NetworkResult.Success -> _uiState.value = AddAccountUiState.Success
                is NetworkResult.Error -> _uiState.value = AddAccountUiState.Error(result.error)
            }
        }
    }
}
