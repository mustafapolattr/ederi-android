package com.mustafa.ederi.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Error(val error: AppError) : AuthUiState()
}

/**
 * Backs both Login and Register — on success, [AuthRepository] flips
 * [com.mustafa.ederi.core.security.SessionManager] and the nav root swaps
 * to the main graph on its own, so this ViewModel doesn't need a
 * success-navigation callback.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.login(email.trim(), password)) {
                is NetworkResult.Success -> _uiState.value = AuthUiState.Idle
                is NetworkResult.Error -> _uiState.value = AuthUiState.Error(result.error)
            }
        }
    }

    fun register(email: String, password: String, firstName: String?, lastName: String?) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.register(email.trim(), password, firstName?.trim(), lastName?.trim())) {
                is NetworkResult.Success -> _uiState.value = AuthUiState.Idle
                is NetworkResult.Error -> _uiState.value = AuthUiState.Error(result.error)
            }
        }
    }
}
