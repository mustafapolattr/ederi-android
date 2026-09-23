package com.mustafa.ederi.presentation.screens.goals

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
import com.mustafa.ederi.domain.model.Goal
import com.mustafa.ederi.domain.model.GoalType
import java.math.BigDecimal

@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val createState by viewModel.createState.collectAsState()

    var name by remember { mutableStateOf("") }
    var goalTypeExpanded by remember { mutableStateOf(false) }
    var selectedGoalType by remember { mutableStateOf(GoalType.CUSTOM) }
    var targetAmount by remember { mutableStateOf("") }
    var currentAmount by remember { mutableStateOf("0") }
    var currency by remember { mutableStateOf("USD") }
    var targetDate by remember { mutableStateOf("") }

    LaunchedEffect(createState) {
        if (createState is CreateGoalUiState.Success) {
            name = ""
            targetAmount = ""
            currentAmount = "0"
            targetDate = ""
            viewModel.resetCreateState()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Goals", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is GoalsUiState.Loading -> CircularProgressIndicator()
            is GoalsUiState.Empty -> Text("No goals yet.", style = MaterialTheme.typography.bodyMedium)
            is GoalsUiState.Error -> Text(state.error.userMessage, color = MaterialTheme.colorScheme.error)
            is GoalsUiState.Content -> Column {
                state.goals.forEach { goal -> GoalRowItem(goal) }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Add goal", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Box {
            OutlinedButton(onClick = { goalTypeExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Type: ${selectedGoalType.name}")
            }
            DropdownMenu(expanded = goalTypeExpanded, onDismissRequest = { goalTypeExpanded = false }) {
                GoalType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            selectedGoalType = type
                            goalTypeExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = targetAmount,
            onValueChange = { newValue -> if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) targetAmount = newValue },
            label = { Text("Target amount") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = currentAmount,
            onValueChange = { newValue -> if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) currentAmount = newValue },
            label = { Text("Current amount") },
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
            value = targetDate,
            onValueChange = { targetDate = it },
            label = { Text("Target date (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (createState is CreateGoalUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (createState as CreateGoalUiState.Error).error.userMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        val parsedTarget = targetAmount.toBigDecimalOrNull()
        val parsedCurrent = currentAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        Button(
            onClick = {
                if (parsedTarget != null) {
                    viewModel.createGoal(
                        name = name.trim(),
                        goalType = selectedGoalType,
                        targetAmount = parsedTarget,
                        currentAmount = parsedCurrent,
                        currency = currency.trim(),
                        targetDate = targetDate.trim()
                    )
                }
            },
            enabled = createState !is CreateGoalUiState.Loading &&
                name.isNotBlank() && parsedTarget != null &&
                currency.isNotBlank() && targetDate.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (createState is CreateGoalUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Add goal")
            }
        }
    }
}

@Composable
private fun GoalRowItem(goal: Goal) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = goal.name, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "${goal.currentAmount} / ${goal.targetAmount} ${goal.currency}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(text = "Remaining ${goal.remainingAmount} ${goal.currency}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Needed/month ${goal.requiredMonthlyContribution} ${goal.currency}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(text = "Target date ${goal.targetDate}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
