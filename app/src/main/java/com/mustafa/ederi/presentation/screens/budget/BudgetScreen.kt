package com.mustafa.ederi.presentation.screens.budget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafa.ederi.domain.model.Category
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val createState by viewModel.createState.collectAsState()

    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("USD") }
    var startDate by remember { mutableStateOf(firstDayOfThisMonth()) }
    var endDate by remember { mutableStateOf(lastDayOfThisMonth()) }

    LaunchedEffect(createState) {
        if (createState is CreateBudgetUiState.Success) {
            amount = ""
            viewModel.resetCreateState()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Budget", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is BudgetsUiState.Loading -> CircularProgressIndicator()
            is BudgetsUiState.Empty -> Text("No budgets yet.", style = MaterialTheme.typography.bodyMedium)
            is BudgetsUiState.Error -> Text(state.error.userMessage, color = MaterialTheme.colorScheme.error)
            is BudgetsUiState.Content -> Column {
                state.rows.forEach { row -> BudgetRowItem(row) }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Add budget", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            OutlinedButton(onClick = { categoryExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Category: ${selectedCategory?.name ?: "Select"}")
            }
            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { newValue -> if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) amount = newValue },
            label = { Text("Amount") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = currency,
            onValueChange = { currency = it.uppercase() },
            label = { Text("Currency (e.g. USD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start date (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("End date (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (createState is CreateBudgetUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (createState as CreateBudgetUiState.Error).error.userMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        val parsedAmount = amount.toBigDecimalOrNull()
        val category = selectedCategory
        Button(
            onClick = {
                if (category != null && parsedAmount != null) {
                    viewModel.createBudget(category.id, parsedAmount, currency.trim(), startDate.trim(), endDate.trim())
                }
            },
            enabled = createState !is CreateBudgetUiState.Loading &&
                category != null && parsedAmount != null &&
                currency.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (createState is CreateBudgetUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Add budget")
            }
        }
    }
}

@Composable
private fun BudgetRowItem(row: BudgetRow) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = row.categoryName, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "Spent ${row.budget.spent} / ${row.budget.amount} ${row.budget.currency}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(text = "Remaining ${row.budget.remaining} ${row.budget.currency}", style = MaterialTheme.typography.bodySmall)
            Text(text = "${row.budget.startDate} – ${row.budget.endDate}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun firstDayOfThisMonth(): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
}

private fun lastDayOfThisMonth(): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
}
