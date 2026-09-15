package com.mustafa.ederi.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mustafa.ederi.presentation.screens.accounts.AccountsScreen
import com.mustafa.ederi.presentation.screens.accounts.AddAccountScreen
import com.mustafa.ederi.presentation.screens.ai.AiAssistantScreen
import com.mustafa.ederi.presentation.screens.auth.LoginScreen
import com.mustafa.ederi.presentation.screens.auth.RegisterScreen
import com.mustafa.ederi.presentation.screens.budget.BudgetScreen
import com.mustafa.ederi.presentation.screens.goals.GoalsScreen
import com.mustafa.ederi.presentation.screens.home.HomeScreen
import com.mustafa.ederi.presentation.screens.transactions.AddTransactionScreen
import com.mustafa.ederi.presentation.screens.transactions.TransactionsScreen

/**
 * Root switch between the auth graph and the main graph, driven by
 * [SessionViewModel.isLoggedIn]. Swapping the composable outright (rather
 * than nesting both graphs under one NavHost) means a forced logout from
 * [com.mustafa.ederi.core.network.TokenAuthenticator] always lands on a
 * fresh Login screen with no leftover back stack.
 */
@Composable
fun EderiNavHost(sessionViewModel: SessionViewModel = hiltViewModel()) {
    val isLoggedIn by sessionViewModel.isLoggedIn.collectAsState()
    if (isLoggedIn) MainGraph() else AuthGraph()
}

@Composable
private fun AuthGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destination.Login.route) {
        composable(Destination.Login.route) {
            LoginScreen(onNavigateToRegister = { navController.navigate(Destination.Register.route) })
        }
        composable(Destination.Register.route) {
            RegisterScreen(onNavigateToLogin = { navController.popBackStack() })
        }
    }
}

@Composable
private fun MainGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { EderiBottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Home.route) {
                HomeScreen(onNavigateToAccounts = { navController.navigate(Destination.Accounts.route) })
            }
            composable(Destination.Transactions.route) {
                TransactionsScreen(onAddTransaction = { navController.navigate(Destination.AddTransaction.route) })
            }
            composable(Destination.Budget.route) { BudgetScreen() }
            composable(Destination.Goals.route) { GoalsScreen() }
            composable(Destination.AiAssistant.route) { AiAssistantScreen() }
            composable(Destination.Accounts.route) {
                AccountsScreen(
                    onAddAccount = { navController.navigate(Destination.AddAccount.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Destination.AddAccount.route) {
                AddAccountScreen(onDone = { navController.popBackStack() })
            }
            composable(Destination.AddTransaction.route) {
                AddTransactionScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}
