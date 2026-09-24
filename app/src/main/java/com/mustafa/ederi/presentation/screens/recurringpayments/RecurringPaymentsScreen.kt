package com.mustafa.ederi.presentation.screens.recurringpayments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.mustafa.ederi.domain.model.RecurringPayment
import com.mustafa.ederi.domain.model.RecurringPaymentFrequency
import com.mustafa.ederi.domain.model.RecurringPaymentType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringPaymentsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecurringPaymentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val createState by viewModel.createState.collectAsState()

    var selectedType by remember { mutableStateOf(RecurringPaymentType.EXPENSE) }
    var selectedAccount by remember(accounts) { mutableStateOf(accounts.firstOrNull()) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    var selectedFrequency by remember { mutableStateOf(RecurringPaymentFrequency.MONTHLY) }
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var nextPaymentDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
    }

    var typeExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var frequencyExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(createState) {
        if (createState is CreateRecurringPaymentUiState.Success) {
            name = ""
            amount = ""
            viewModel.resetCreateState()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Recurring Payments") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (val state = uiState) {
                is RecurringPaymentsUiState.Loading -> CircularProgressIndicator()
                is RecurringPaymentsUiState.Empty -> Text("No recurring payments yet.", style = MaterialTheme.typography.bodyMedium)
                is RecurringPaymentsUiState.Error -> Text(state.error.userMessage, color = MaterialTheme.colorScheme.error)
                is RecurringPaymentsUiState.Content -> Column {
                    state.payments.forEach { payment ->
                        RecurringPaymentRow(payment, onDelete = { viewModel.deleteRecurringPayment(payment.id) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Add recurring payment", style = MaterialTheme.typography.titleMedium)
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
                OutlinedButton(onClick = { typeExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Type: ${selectedType.name}")
                }
                DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    RecurringPaymentType.entries.forEach { type ->
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
                OutlinedButton(onClick = { frequencyExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Frequency: ${selectedFrequency.name}")
                }
                DropdownMenu(expanded = frequencyExpanded, onDismissRequest = { frequencyExpanded = false }) {
                    RecurringPaymentFrequency.entries.forEach { frequency ->
                        DropdownMenuItem(
                            text = { Text(frequency.name) },
                            onClick = {
                                selectedFrequency = frequency
                                frequencyExpanded = false
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
                onValueChange = { newValue -> if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) amount = newValue },
                label = { Text("Amount") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nextPaymentDate,
                onValueChange = { nextPaymentDate = it },
                label = { Text("Next payment date (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (createState is CreateRecurringPaymentUiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (createState as CreateRecurringPaymentUiState.Error).error.userMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            val parsedAmount = amount.toBigDecimalOrNull()
            val account = selectedAccount
            Button(
                onClick = {
                    if (account != null && parsedAmount != null) {
                        viewModel.createRecurringPayment(
                            name = name.trim(),
                            type = selectedType,
                            amount = parsedAmount,
                            currency = account.currency,
                            frequency = selectedFrequency,
                            nextPaymentDate = nextPaymentDate.trim(),
                            categoryId = selectedCategoryId,
                            accountId = account.id
                        )
                    }
                },
                enabled = createState !is CreateRecurringPaymentUiState.Loading &&
                    name.isNotBlank() && account != null && parsedAmount != null && nextPaymentDate.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (createState is CreateRecurringPaymentUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Add recurring payment")
                }
            }
        }
    }
}

@Composable
private fun RecurringPaymentRow(payment: RecurringPayment, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = payment.name, style = MaterialTheme.typography.titleSmall)
                TextButton(onClick = onDelete) { Text("Delete") }
            }
            Text(
                text = "${payment.type.name} – ${payment.amount} ${payment.currency}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(text = "${payment.frequency.name}, next: ${payment.nextPaymentDate}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
