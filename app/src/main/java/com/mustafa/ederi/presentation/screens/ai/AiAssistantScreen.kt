package com.mustafa.ederi.presentation.screens.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.model.Category
import com.mustafa.ederi.domain.model.ParseTransactionResult
import com.mustafa.ederi.presentation.components.CurrencyDropdownField
import com.mustafa.ederi.presentation.components.DEFAULT_CURRENCY

@Composable
fun AiAssistantScreen(modifier: Modifier = Modifier, viewModel: AiViewModel = hiltViewModel()) {
    val messages by viewModel.messages.collectAsState()
    val chatSendState by viewModel.chatSendState.collectAsState()
    val parseState by viewModel.parseState.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val scenarioState by viewModel.scenarioState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var chatInput by remember { mutableStateOf("") }
    var parseInput by remember { mutableStateOf("") }
    var scenarioAmount by remember { mutableStateOf("") }
    var scenarioCurrency by remember { mutableStateOf(DEFAULT_CURRENCY) }

    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item { Text(text = "AI Assistant", style = MaterialTheme.typography.headlineSmall) }
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { Text(text = "Chat", style = MaterialTheme.typography.titleMedium) }
        item { Spacer(modifier = Modifier.height(4.dp)) }
        items(messages) { message -> ChatMessageRow(message) }
        if (chatSendState is ChatSendState.Sending) {
            item { CircularProgressIndicator(modifier = Modifier.size(20.dp)) }
        }
        if (chatSendState is ChatSendState.Error) {
            item {
                Text(
                    text = (chatSendState as ChatSendState.Error).error.userMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    label = { Text("Ask about your finances") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.sendChatMessage(chatInput)
                        chatInput = ""
                    },
                    enabled = chatInput.isNotBlank() && chatSendState !is ChatSendState.Sending
                ) {
                    Text("Send")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { Text(text = "Add via natural language", style = MaterialTheme.typography.titleMedium) }
        item { Spacer(modifier = Modifier.height(4.dp)) }
        item {
            OutlinedTextField(
                value = parseInput,
                onValueChange = { parseInput = it },
                label = { Text("e.g. \"Spent \$42.50 at Starbucks yesterday\"") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            Button(
                onClick = { viewModel.parseTransaction(parseInput) },
                enabled = parseInput.isNotBlank() && parseState !is ParseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (parseState is ParseUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Parse")
                }
            }
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        when (val state = parseState) {
            is ParseUiState.Preview -> item {
                ParsePreviewCard(
                    result = state.result,
                    accounts = accounts,
                    categories = categories,
                    saveState = saveState,
                    onSave = viewModel::saveParsedTransaction,
                    onDiscard = {
                        viewModel.resetParse()
                        parseInput = ""
                    }
                )
            }
            is ParseUiState.Error -> item {
                Text(
                    text = state.error.userMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            else -> Unit
        }
        if (saveState is SaveUiState.Saved) {
            item {
                Text(
                    text = "Transaction saved.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (saveState is SaveUiState.Error) {
            item {
                Text(
                    text = (saveState as SaveUiState.Error).error.userMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { Text(text = "What if I spend...?", style = MaterialTheme.typography.titleMedium) }
        item { Spacer(modifier = Modifier.height(4.dp)) }
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = scenarioAmount,
                    onValueChange = { newValue -> if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) scenarioAmount = newValue },
                    label = { Text("Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                CurrencyDropdownField(
                    selected = scenarioCurrency,
                    onSelected = { scenarioCurrency = it },
                    modifier = Modifier.width(140.dp)
                )
            }
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            val amount = scenarioAmount.toBigDecimalOrNull()
            Button(
                onClick = { if (amount != null) viewModel.runScenario(amount, scenarioCurrency, "") },
                enabled = amount != null && scenarioCurrency.isNotBlank() && scenarioState !is ScenarioUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (scenarioState is ScenarioUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Run scenario")
                }
            }
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        when (val state = scenarioState) {
            is ScenarioUiState.Result -> item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Available now: ${state.result.currentAvailableToSpend} ${state.result.currency}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "After spending ${state.result.hypotheticalAmount}: ${state.result.projectedAvailableToSpend} ${state.result.currency}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = state.result.explanation, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            is ScenarioUiState.Error -> item {
                Text(
                    text = state.error.userMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun ChatMessageRow(message: ChatMessage) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = if (message.fromUser) "You: " else "AI: ",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(text = message.text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ParsePreviewCard(
    result: ParseTransactionResult,
    accounts: List<Account>,
    categories: List<Category>,
    saveState: SaveUiState,
    onSave: () -> Unit,
    onDiscard: () -> Unit
) {
    val transaction = result.transaction
    val accountName = accounts.firstOrNull { it.id == transaction.accountId }?.name
    val categoryName = categories.firstOrNull { it.id == transaction.categoryId }?.name

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Review before saving", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${transaction.type.name} ${transaction.amount} ${transaction.currency ?: ""}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Account: ${accountName ?: "Unresolved"}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Category: ${categoryName ?: "None"}", style = MaterialTheme.typography.bodySmall)
            if (transaction.merchant.isNotBlank()) {
                Text(text = "Merchant: ${transaction.merchant}", style = MaterialTheme.typography.bodySmall)
            }
            if (transaction.description.isNotBlank()) {
                Text(text = "Description: ${transaction.description}", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "Date: ${transaction.transactionDate}", style = MaterialTheme.typography.bodySmall)

            if (result.needsClarification != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = result.needsClarification, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            if (result.errors != null) {
                Spacer(modifier = Modifier.height(4.dp))
                result.errors.forEach { (field, messages) ->
                    Text(text = "$field: ${messages.firstOrNull()}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(onClick = onDiscard) { Text("Discard") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSave, enabled = result.valid && saveState !is SaveUiState.Saving) {
                    if (saveState is SaveUiState.Saving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Save")
                    }
                }
            }
        }
    }
}
