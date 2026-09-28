package com.mustafa.ederi.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.ederi.core.error.AppError
import com.mustafa.ederi.core.network.NetworkResult
import com.mustafa.ederi.domain.model.Account
import com.mustafa.ederi.domain.model.Category
import com.mustafa.ederi.domain.model.ParseTransactionResult
import com.mustafa.ederi.domain.model.ScenarioResult
import com.mustafa.ederi.domain.repository.AccountRepository
import com.mustafa.ederi.domain.repository.AiRepository
import com.mustafa.ederi.domain.repository.CategoryRepository
import com.mustafa.ederi.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class ChatMessage(val fromUser: Boolean, val text: String)

sealed class ChatSendState {
    data object Idle : ChatSendState()
    data object Sending : ChatSendState()
    data class Error(val error: AppError) : ChatSendState()
}

sealed class ParseUiState {
    data object Idle : ParseUiState()
    data object Loading : ParseUiState()
    data class Preview(val result: ParseTransactionResult) : ParseUiState()
    data class Error(val error: AppError) : ParseUiState()
}

sealed class SaveUiState {
    data object Idle : SaveUiState()
    data object Saving : SaveUiState()
    data object Saved : SaveUiState()
    data class Error(val error: AppError) : SaveUiState()
}

sealed class ScenarioUiState {
    data object Idle : ScenarioUiState()
    data object Loading : ScenarioUiState()
    data class Result(val result: ScenarioResult) : ScenarioUiState()
    data class Error(val error: AppError) : ScenarioUiState()
}

/**
 * Backs the whole AI tab (spec §20-21, §28-31): chat, natural-language
 * transaction entry, and what-if scenarios. AI output never reaches
 * [TransactionRepository] except through [saveParsedTransaction], which the
 * user must trigger explicitly after reviewing the preview (spec §20/§30).
 */
@HiltViewModel
class AiViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val accounts: StateFlow<List<Account>> =
        accountRepository.observeAccounts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> =
        categoryRepository.observeCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var conversationId: String? = null

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _chatSendState = MutableStateFlow<ChatSendState>(ChatSendState.Idle)
    val chatSendState: StateFlow<ChatSendState> = _chatSendState.asStateFlow()

    private val _parseState = MutableStateFlow<ParseUiState>(ParseUiState.Idle)
    val parseState: StateFlow<ParseUiState> = _parseState.asStateFlow()

    private val _saveState = MutableStateFlow<SaveUiState>(SaveUiState.Idle)
    val saveState: StateFlow<SaveUiState> = _saveState.asStateFlow()

    private val _scenarioState = MutableStateFlow<ScenarioUiState>(ScenarioUiState.Idle)
    val scenarioState: StateFlow<ScenarioUiState> = _scenarioState.asStateFlow()

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        _messages.value = _messages.value + ChatMessage(fromUser = true, text = text)
        _chatSendState.value = ChatSendState.Sending
        viewModelScope.launch {
            when (val result = aiRepository.chat(text, conversationId)) {
                is NetworkResult.Success -> {
                    conversationId = result.data.conversationId
                    _messages.value = _messages.value + ChatMessage(fromUser = false, text = result.data.message)
                    _chatSendState.value = ChatSendState.Idle
                }
                is NetworkResult.Error -> _chatSendState.value = ChatSendState.Error(result.error)
            }
        }
    }

    fun parseTransaction(text: String) {
        if (text.isBlank()) return
        _parseState.value = ParseUiState.Loading
        _saveState.value = SaveUiState.Idle
        viewModelScope.launch {
            _parseState.value = when (val result = aiRepository.parseTransaction(text)) {
                is NetworkResult.Success -> ParseUiState.Preview(result.data)
                is NetworkResult.Error -> ParseUiState.Error(result.error)
            }
        }
    }

    /** Only ever called from an explicit user tap on the preview's Save button — never automatically. */
    fun saveParsedTransaction() {
        val preview = (_parseState.value as? ParseUiState.Preview)?.result ?: return
        val accountId = preview.transaction.accountId ?: return
        val currency = preview.transaction.currency ?: return
        if (!preview.valid) return

        _saveState.value = SaveUiState.Saving
        viewModelScope.launch {
            val result = transactionRepository.createTransaction(
                accountId = accountId,
                toAccountId = null,
                categoryId = preview.transaction.categoryId,
                type = preview.transaction.type,
                amount = preview.transaction.amount,
                currency = currency,
                merchant = preview.transaction.merchant.ifBlank { null },
                description = preview.transaction.description.ifBlank { null },
                notes = null,
                transactionDate = preview.transaction.transactionDate
            )
            _saveState.value = when (result) {
                is NetworkResult.Success -> {
                    _parseState.value = ParseUiState.Idle
                    SaveUiState.Saved
                }
                is NetworkResult.Error -> SaveUiState.Error(result.error)
            }
        }
    }

    fun resetParse() {
        _parseState.value = ParseUiState.Idle
        _saveState.value = SaveUiState.Idle
    }

    fun runScenario(amount: BigDecimal, currency: String, description: String) {
        _scenarioState.value = ScenarioUiState.Loading
        viewModelScope.launch {
            _scenarioState.value = when (val result = aiRepository.scenario(amount, currency, description)) {
                is NetworkResult.Success -> ScenarioUiState.Result(result.data)
                is NetworkResult.Error -> ScenarioUiState.Error(result.error)
            }
        }
    }
}
