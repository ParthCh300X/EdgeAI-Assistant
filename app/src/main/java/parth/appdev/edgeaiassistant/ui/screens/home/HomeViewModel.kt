package parth.appdev.edgeaiassistant.ui.screens.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parth.appdev.edgeaiassistant.analytics.AnalyticsManager
import parth.appdev.edgeaiassistant.data.repository.ChatRepository
import parth.appdev.edgeaiassistant.domain.dispatcher.CommandDispatcher
import parth.appdev.edgeaiassistant.domain.intent.IntentType
import parth.appdev.edgeaiassistant.engine.context.ContextManager
import parth.appdev.edgeaiassistant.engine.ml.IntentClassifier
import parth.appdev.edgeaiassistant.engine.ml.IntentMapper
import parth.appdev.edgeaiassistant.engine.rules.RuleEngine
import parth.appdev.edgeaiassistant.personalization.PersonalizationManager
import parth.appdev.edgeaiassistant.ui.state.ChatMessage
import parth.appdev.edgeaiassistant.ui.state.HomeUiState
import parth.appdev.edgeaiassistant.util.InputSanitizer
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val dispatcher: CommandDispatcher,
    private val analytics: AnalyticsManager,
    private val personalization: PersonalizationManager,
    private val chatRepository: ChatRepository
) : AndroidViewModel(application) {

    private val ruleEngine     = RuleEngine()
    private val contextManager = ContextManager()
    private val mlClassifier   = IntentClassifier(application)

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        loadHistory()
    }

    // ── Load persisted chat history on cold start ──────────────────────────
    private fun loadHistory() {
        viewModelScope.launch {
            val history = chatRepository.loadHistory()
            if (history.isEmpty()) {
                loadPersonalizedGreeting()
            } else {
                _state.update { it.copy(messages = history) }
                refreshSuggestions()
            }
        }
    }

    // ── Text input ─────────────────────────────────────────────────────────
    fun onTextChanged(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    // ── Main send pipeline ─────────────────────────────────────────────────
    fun onSend() {
        val raw   = _state.value.inputText
        val input = InputSanitizer.sanitize(raw) ?: return

        // Step 1 — Rule engine
        val ruleResult  = ruleEngine.detectIntent(input)
        var finalIntent = ruleResult.intent
        var finalInput  = input

        // Step 2 — Context (alarm follow-up)
        if (finalIntent == IntentType.GENERAL && contextManager.isValid()) {
            val last = contextManager.get()
            if (last.lastIntent == IntentType.SET_ALARM && last.isPending) {
                when {
                    input.matches(Regex("\\d{1,2}")) -> {
                        finalIntent = IntentType.SET_ALARM
                        finalInput  = (last.lastInput ?: "") + " $input pm"
                    }
                    looksLikeTimeInput(input) -> {
                        finalIntent = IntentType.SET_ALARM
                        finalInput  = (last.lastInput ?: "") + " $input"
                    }
                }
            }
        }

        // Step 3 — ML fallback with adaptive confidence
        if (finalIntent == IntentType.GENERAL) {
            val (mlIndex, confidence) = mlClassifier.predict(input)
            val mlIntent = IntentMapper.map(mlIndex)

            when {
                confidence >= 0.65f -> {
                    finalIntent = mlIntent
                }
                confidence >= 0.4f -> {
                    val clarification = "I'm not sure what you mean. Did you want to: " +
                            mlIntent.name.replace("_", " ").lowercase() + "?"
                    _state.update {
                        it.copy(
                            inputText = "",
                            messages  = it.messages +
                                    ChatMessage(input, true) +
                                    ChatMessage(clarification, false),
                            isTyping      = false,
                            errorMessage  = null
                        )
                    }
                    viewModelScope.launch {
                        chatRepository.save(input, isUser = true)
                        chatRepository.save(clarification, isUser = false)
                    }
                    return
                }
                else -> finalIntent = IntentType.GENERAL
            }
        }

        Log.d("INTENT_DEBUG", "INPUT=$input | FINAL=$finalIntent")

        // Step 4 — Show user bubble + typing indicator immediately
        _state.update {
            it.copy(
                inputText    = "",
                messages     = it.messages + ChatMessage(input, true),
                isTyping     = true,
                errorMessage = null
            )
        }
        viewModelScope.launch { chatRepository.save(input, isUser = true) }

        val capturedIntent = finalIntent
        val capturedInput  = finalInput

        // Step 5 — Execute off main thread
        viewModelScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val command   = dispatcher.dispatch(capturedIntent, capturedInput)

            val response = try {
                command.execute()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Command failed", e)
                null
            }

            val execTime = System.currentTimeMillis() - startTime

            val isHardError = response == null || response.lowercase().let {
                it.contains("invalid") || it.contains("not supported")
            }

            analytics.log(
                intent        = capturedIntent,
                input         = input,
                success       = !isHardError,
                executionTime = execTime
            )

            // Update context
            if (capturedIntent == IntentType.SET_ALARM) {
                if (response?.lowercase()?.contains("set") == true) contextManager.clear()
                else contextManager.update(capturedIntent, capturedInput, pending = true)
            } else {
                contextManager.update(capturedIntent, capturedInput, pending = false)
            }

            val delayMs = when {
                input.length < 10 -> 400L
                input.length < 30 -> 700L
                else              -> 1000L
            }
            delay(delayMs)

            if (isHardError && response != null) {
                _state.update { it.copy(isTyping = false, errorMessage = response) }
            } else {
                val finalResponse = response ?: "Something went wrong. Please try again."
                _state.update {
                    it.copy(
                        messages = it.messages + ChatMessage(finalResponse, false),
                        isTyping = false
                    )
                }
                viewModelScope.launch { chatRepository.save(finalResponse, isUser = false) }
            }

            viewModelScope.launch { refreshSuggestions() }
        }
    }

    // ── Suggestion chips ───────────────────────────────────────────────────
    private fun refreshSuggestions() {
        viewModelScope.launch {
            val top = personalization.getTopIntents()

            // Don't show chips until user has actually used the app
            if (top.isEmpty()) {
                _state.update { it.copy(suggestions = emptyList()) }
                return@launch
            }

            val chips = top.mapNotNull { intent ->
                when (intent) {
                    IntentType.SET_ALARM     -> "Set alarm 7am"
                    IntentType.CALCULATE     -> "Calculate 25x4"
                    IntentType.TAKE_NOTE     -> "Note groceries"
                    IntentType.GET_NOTES     -> "Show notes"
                    IntentType.OPEN_APP      -> "Open YouTube"
                    IntentType.CONVERT_UNITS -> "Convert km"
                    IntentType.WEATHER       -> "Weather"
                    IntentType.TIMER         -> "Timer 5 min"
                    else                     -> null
                }
            }.take(2)   // hard cap at 2

            _state.update { it.copy(suggestions = chips) }
        }
    }

    // ── Error consumed ─────────────────────────────────────────────────────
    fun onErrorShown() = _state.update { it.copy(errorMessage = null) }

    // ── Voice ──────────────────────────────────────────────────────────────
    fun startListening() = _state.update { it.copy(isListening = true, partialText = "") }
    fun stopListening()  = _state.update { it.copy(isListening = false) }
    fun onVoicePartial(text: String) = _state.update { it.copy(partialText = text) }

    fun onVoiceFinal(text: String) {
        _state.update { it.copy(inputText = text, isListening = false, partialText = "") }
        viewModelScope.launch {
            delay(400)
            onSend()
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private fun looksLikeTimeInput(input: String): Boolean {
        val t = input.lowercase()
        return t.contains(Regex("\\d{1,2}(:\\d{2})?")) ||
                t.contains("am") || t.contains("pm") ||
                t.contains("morning") || t.contains("evening") || t.contains("night")
    }

    private fun loadPersonalizedGreeting() {
        viewModelScope.launch {
            val top = personalization.getTopIntents()
            val greeting = if (top.isEmpty()) "What can I do for you?" else {
                when (top.first()) {
                    IntentType.CALCULATE     -> "Need a quick calculation?"
                    IntentType.SET_ALARM     -> "Setting another alarm?"
                    IntentType.TAKE_NOTE     -> "Want me to save something?"
                    IntentType.OPEN_APP      -> "Looking for an app?"
                    IntentType.CONVERT_UNITS -> "Need a conversion?"
                    else                     -> "What can I do for you?"
                }
            }
            _state.update { it.copy(messages = listOf(ChatMessage(greeting, false))) }
            refreshSuggestions()
        }
    }
}