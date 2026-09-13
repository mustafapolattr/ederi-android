package com.mustafa.ederi.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mustafa.ederi.presentation.screens.ai.AiAssistantScreen
import com.mustafa.ederi.presentation.screens.budget.BudgetScreen
import com.mustafa.ederi.presentation.screens.goals.GoalsScreen
import com.mustafa.ederi.presentation.screens.home.HomeScreen
import com.mustafa.ederi.presentation.screens.transactions.TransactionsScreen

@Composable
fun EderiNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { EderiBottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Home.route) { HomeScreen() }
            composable(Destination.Transactions.route) { TransactionsScreen() }
            composable(Destination.Budget.route) { BudgetScreen() }
            composable(Destination.Goals.route) { GoalsScreen() }
            composable(Destination.AiAssistant.route) { AiAssistantScreen() }
        }
    }
}
