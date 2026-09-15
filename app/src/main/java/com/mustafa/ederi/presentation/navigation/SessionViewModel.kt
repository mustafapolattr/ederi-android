package com.mustafa.ederi.presentation.navigation

import androidx.lifecycle.ViewModel
import com.mustafa.ederi.core.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    sessionManager: SessionManager
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedIn
}
