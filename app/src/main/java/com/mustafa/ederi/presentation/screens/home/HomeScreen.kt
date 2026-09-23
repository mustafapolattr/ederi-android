package com.mustafa.ederi.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafa.ederi.domain.model.CurrencyAmount
import com.mustafa.ederi.domain.model.DashboardBudget
import com.mustafa.ederi.domain.model.DashboardData
import com.mustafa.ederi.domain.model.DashboardGoal
import com.mustafa.ederi.domain.model.MonthSummary

@Composable
fun HomeScreen(
    onNavigateToAccounts: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Home", style = MaterialTheme.typography.headlineSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = viewModel::refresh) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
                Button(onClick = onNavigateToAccounts) {
                    Text("Accounts")
                }
            }
        }

        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingState()
            is HomeUiState.Error -> ErrorState(state.error.userMessage)
            is HomeUiState.Content -> DashboardContent(state.data)
        }
    }
}

@Composable
private fun DashboardContent(data: DashboardData, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item { SectionTitle("Balances") }
        if (data.totalBalance.isEmpty()) {
            item { Text("No accounts yet.", style = MaterialTheme.typography.bodySmall) }
        }
        items(data.totalBalance) { balance -> BalanceRow(balance) }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { SectionTitle("This month") }
        if (data.thisMonth.isEmpty()) {
            item { Text("No transactions this month.", style = MaterialTheme.typography.bodySmall) }
        }
        items(data.thisMonth) { summary -> MonthSummaryCard(summary) }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { SectionTitle("Budgets") }
        if (data.budgets.isEmpty()) {
            item { Text("No active budgets.", style = MaterialTheme.typography.bodySmall) }
        }
        items(data.budgets) { budget -> DashboardBudgetCard(budget) }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { SectionTitle("Goals") }
        if (data.goals.isEmpty()) {
            item { Text("No goals yet.", style = MaterialTheme.typography.bodySmall) }
        }
        items(data.goals) { goal -> DashboardGoalCard(goal) }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
}

@Composable
private fun BalanceRow(balance: CurrencyAmount) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = balance.currency, style = MaterialTheme.typography.bodyMedium)
        Text(text = balance.amount.toString(), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MonthSummaryCard(summary: MonthSummary) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = summary.currency, style = MaterialTheme.typography.titleSmall)
            Text(text = "Income ${summary.income}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Expense ${summary.expense}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Saved ${summary.saved}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DashboardBudgetCard(budget: DashboardBudget) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = budget.category, style = MaterialTheme.typography.titleSmall)
            Text(text = "Spent ${budget.spent} / ${budget.amount} ${budget.currency}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Remaining ${budget.remaining} ${budget.currency}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DashboardGoalCard(goal: DashboardGoal) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = goal.name, style = MaterialTheme.typography.titleSmall)
            Text(text = "${goal.currentAmount} / ${goal.targetAmount} ${goal.currency}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Remaining ${goal.remainingAmount} ${goal.currency}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Needed/month ${goal.requiredMonthlyContribution} ${goal.currency}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}
