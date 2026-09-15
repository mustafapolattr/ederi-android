package com.mustafa.ederi.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String) {
    data object Login : Destination("login")
    data object Register : Destination("register")
    data object Home : Destination("home")
    data object Transactions : Destination("transactions")
    data object Budget : Destination("budget")
    data object Goals : Destination("goals")
    data object AiAssistant : Destination("ai_assistant")
    data object Accounts : Destination("accounts")
    data object AddAccount : Destination("accounts/add")
    data object AddTransaction : Destination("transactions/add")
}

data class BottomNavItem(
    val destination: Destination,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Destination.Home, "Home", Icons.Filled.Home),
    BottomNavItem(Destination.Transactions, "Transactions", Icons.Filled.SwapHoriz),
    BottomNavItem(Destination.Budget, "Budget", Icons.Filled.AccountBalanceWallet),
    BottomNavItem(Destination.Goals, "Goals", Icons.Filled.Flag),
    BottomNavItem(Destination.AiAssistant, "AI", Icons.Filled.AutoAwesome)
)
