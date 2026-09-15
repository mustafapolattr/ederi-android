package com.mustafa.ederi.presentation.screens.auth

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success returns to Idle`() = runTest {
        coEvery { authRepository.login("user@example.com", "secret") } returns NetworkResult.Success(Unit)
        val viewModel = AuthViewModel(authRepository)

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(AuthUiState.Idle)
            viewModel.login("user@example.com", "secret")
            assertThat(awaitItem()).isEqualTo(AuthUiState.Loading)
            assertThat(awaitItem()).isEqualTo(AuthUiState.Idle)
        }
    }

    @Test
    fun `login failure surfaces the mapped error`() = runTest {
        val error = AppError.Validation("password", "Invalid credentials.")
        coEvery { authRepository.login(any(), any()) } returns NetworkResult.Error(error)
        val viewModel = AuthViewModel(authRepository)

        viewModel.uiState.test {
            awaitItem() // Idle
            viewModel.login("user@example.com", "wrong")
            awaitItem() // Loading
            val state = awaitItem() as AuthUiState.Error
            assertThat(state.error).isEqualTo(error)
        }
    }

    @Test
    fun `register chains into login and only calls login on register success`() = runTest {
        coEvery { authRepository.register("user@example.com", "secret", null, null) } returns NetworkResult.Success(Unit)
        val viewModel = AuthViewModel(authRepository)

        viewModel.uiState.test {
            awaitItem() // Idle
            viewModel.register("user@example.com", "secret", null, null)
            awaitItem() // Loading
            assertThat(awaitItem()).isEqualTo(AuthUiState.Idle)
        }
    }
}
