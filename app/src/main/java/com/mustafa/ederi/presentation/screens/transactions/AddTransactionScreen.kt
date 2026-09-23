package com.mustafa.ederi.presentation.screens.transactions

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
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddTransactionScreen(
    onDone: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var selectedAccount by remember(accounts) { mutableStateOf(accounts.firstOrNull()) }
    var selectedToAccount by remember { mutableStateOf<Account?>(null) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var transactionDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
    }

    var accountExpanded by remember { mutableStateOf(false) }
    var toAccountExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AddTransactionUiState.Success) onDone()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Add transaction", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Box {
            OutlinedButton(onClick = { typeExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Type: ${selectedType.name}")
            }
            DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                TransactionType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            selectedType = type
                            typeExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Box {
            OutlinedButton(onClick = { accountExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Account: ${selectedAccount?.name ?: "Select"}")
            }
            DropdownMenu(expanded = accountExpanded, onDismissRequest = { accountExpanded = false }) {
                accounts.forEach { account ->
                    DropdownMenuItem(
                        text = { Text(account.name) },
                        onClick = {
                            selectedAccount = account
                            accountExpanded = false
                        }
                    )
                }
            }
        }

        if (selectedType == TransactionType.TRANSFER) {
            Spacer(modifier = Modifier.height(12.dp))
            Box {
                OutlinedButton(onClick = { toAccountExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("To account: ${selectedToAccount?.name ?: "Select"}")
                }
                DropdownMenu(expanded = toAccountExpanded, onDismissRequest = { toAccountExpanded = false }) {
                    accounts.filter { it.id != selectedAccount?.id }.forEach { account ->
                        DropdownMenuItem(
                            text = { Text(account.name) },
                            onClick = {
                                selectedToAccount = account
                                toAccountExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box {
            OutlinedButton(onClick = { categoryExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Category: ${selectedCategoryName ?: "None"}")
            }
            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                DropdownMenuItem(
                    text = { Text("None") },
                    onClick = {
                        selectedCategoryId = null
                        selectedCategoryName = null
                        categoryExpanded = false
                    }
                )
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategoryId = category.id
                            selectedCategoryName = category.name
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) amount = newValue
            },
            label = { Text("Amount") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = transactionDate,
            onValueChange = { transactionDate = it },
            label = { Text("Date (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = merchant,
            onValueChange = { merchant = it },
            label = { Text("Merchant (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState is AddTransactionUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            val error = (uiState as AddTransactionUiState.Error).error
            val message = if (error is AppError.Validation && error.field != null) {
                "${fieldLabel(error.field)}: ${error.userMessage}"
            } else {
                error.userMessage
            }
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        val parsedAmount = amount.toBigDecimalOrNull()
        val account = selectedAccount
        val isTransferValid = selectedType != TransactionType.TRANSFER || selectedToAccount != null
        Button(
            onClick = {
                if (account != null && parsedAmount != null) {
                    viewModel.createTransaction(
                        accountId = account.id,
                        toAccountId = if (selectedType == TransactionType.TRANSFER) selectedToAccount?.id else null,
                        categoryId = selectedCategoryId,
                        type = selectedType,
                        amount = parsedAmount,
                        currency = account.currency,
                        merchant = merchant.ifBlank { null },
                        description = description.ifBlank { null },
                        notes = notes.ifBlank { null },
                        transactionDate = transactionDate
                    )
                }
            },
            enabled = uiState !is AddTransactionUiState.Loading &&
                account != null && parsedAmount != null && transactionDate.isNotBlank() && isTransferValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is AddTransactionUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Save")
            }
        }
    }
}

/** Maps the backend's §41 error envelope field name to the label shown next to it on this screen. */
private fun fieldLabel(field: String): String = when (field) {
    "account_id" -> "Account"
    "to_account_id" -> "To account"
    "category_id" -> "Category"
    "type" -> "Type"
    "amount" -> "Amount"
    "currency" -> "Currency"
    "merchant" -> "Merchant"
    "description" -> "Description"
    "notes" -> "Notes"
    "transaction_date" -> "Date"
    else -> field
}
