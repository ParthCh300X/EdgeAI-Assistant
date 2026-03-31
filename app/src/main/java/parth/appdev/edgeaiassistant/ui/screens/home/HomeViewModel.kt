package parth.appdev.edgeaiassistant.ui.screens.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import parth.appdev.edgeaiassistant.analytics.AnalyticsManager
import parth.appdev.edgeaiassistant.domain.dispatcher.CommandDispatcher
import parth.appdev.edgeaiassistant.domain.intent.IntentType
import parth.appdev.edgeaiassistant.engine.context.ContextManager
import parth.appdev.edgeaiassistant.engine.ml.IntentClassifier
import parth.appdev.edgeaiassistant.engine.ml.IntentMapper
import parth.appdev.edgeaiassistant.engine.rules.RuleEngine
import parth.appdev.edgeaiassistant.personalization.PersonalizationManager
import parth.appdev.edgeaiassistant.ui.state.ChatMessage
import parth.appdev.edgeaiassistant.ui.state.HomeUiState

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val ruleEngine = RuleEngine()
    private val contextManager = ContextManager()
    private val mlClassifier = IntentClassifier(application)
    private val dispatcher = CommandDispatcher(application)
    private val analytics = AnalyticsManager(application)
    private val personalization = PersonalizationManager(application)

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        loadPersonalizedGreeting()
    }

    fun onTextChanged(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun onSend() {

        val input = _state.value.inputText
        if (input.isBlank()) return

        val ruleResult = ruleEngine.detectIntent(input)

        var finalIntent = ruleResult.intent
        var finalInput = input

        // 🔥 CONTEXT (ALARM FLOW)
        if (ruleResult.intent == IntentType.GENERAL && contextManager.isValid()) {

            val last = contextManager.get()

            if (last.lastIntent == IntentType.SET_ALARM && last.isPending) {

                if (input.matches(Regex("\\d{1,2}"))) {
                    finalIntent = IntentType.SET_ALARM
                    finalInput = (last.lastInput ?: "") + " $input pm"
                } else if (looksLikeTimeInput(input)) {
                    finalIntent = IntentType.SET_ALARM
                    finalInput = (last.lastInput ?: "") + " $input"
                }
            }
        }

        // 🔥 ML FALLBACK
        if (finalIntent == IntentType.GENERAL) {

            val (mlIndex, confidence) = mlClassifier.predict(input)
            val mlIntent = IntentMapper.map(mlIndex)

            if (confidence > 0.75f) {
                finalIntent = mlIntent
            }
        }

        // 🔥 DEBUG
        Log.d("INTENT_DEBUG", """
INPUT: $input
RULE_INTENT: ${ruleResult.intent}
FINAL_INTENT: $finalIntent
FINAL_INPUT: $finalInput
""".trimIndent())

        // 🔥 EXECUTION
        val startTime = System.currentTimeMillis()

        val command = dispatcher.dispatch(finalIntent, finalInput)
        val response = command.execute()

        val endTime = System.currentTimeMillis()

        val success = !response.lowercase().contains("invalid") &&
                !response.lowercase().contains("not supported") &&
                !response.lowercase().contains("couldn't")

        analytics.log(
            intent = finalIntent,
            input = input,
            success = success,
            executionTime = endTime - startTime
        )

        // 🔥 CONTEXT MANAGEMENT
        if (finalIntent == IntentType.SET_ALARM) {

            if (response.lowercase().contains("set")) {
                contextManager.clear()
            } else {
                contextManager.update(finalIntent, finalInput, pending = true)
            }

        } else {
            contextManager.update(finalIntent, finalInput, pending = false)
        }

        // 🔥 STEP 1 — SHOW USER MESSAGE + START TYPING
        _state.update {
            it.copy(
                inputText = "",
                messages = it.messages + ChatMessage(input, true),
                isTyping = true
            )
        }

        // 🔥 STEP 2 — DELAY + AI RESPONSE
        scope.launch {

            val delayTime = when {
                input.length < 10 -> 400L
                input.length < 30 -> 700L
                else -> 1000L
            }

            delay(delayTime)

            _state.update {
                it.copy(
                    messages = it.messages + ChatMessage(response, false),
                    isTyping = false
                )
            }
        }
    }

    private fun looksLikeTimeInput(input: String): Boolean {
        val text = input.lowercase()

        return text.contains(Regex("\\d{1,2}(:\\d{2})?")) ||
                text.contains("am") ||
                text.contains("pm") ||
                text.contains("morning") ||
                text.contains("evening") ||
                text.contains("night")
    }

    // 🔥 VOICE

    fun startListening() {
        _state.update {
            it.copy(isListening = true, partialText = "")
        }
    }

    fun stopListening() {
        _state.update {
            it.copy(isListening = false)
        }
    }

    fun onVoicePartial(text: String) {
        _state.update {
            it.copy(partialText = text)
        }
    }

    fun onVoiceFinal(text: String) {
        _state.update {
            it.copy(
                inputText = text,
                isListening = false,
                partialText = ""
            )
        }

        scope.launch {
            delay(400)
            onSend()
        }
    }

    // 🔥 PERSONALIZED GREETING
    private fun loadPersonalizedGreeting() {

        scope.launch {

            val top = personalization.getTopIntents()

            val greeting = if (top.isEmpty()) {
                "What can I do for you?"
            } else {
                when (top.first()) {
                    IntentType.CALCULATE -> "Need a quick calculation?"
                    IntentType.SET_ALARM -> "Setting another alarm?"
                    IntentType.TAKE_NOTE -> "Want me to save something?"
                    IntentType.OPEN_APP -> "Looking for an app?"
                    IntentType.CONVERT_UNITS -> "Need a conversion?"
                    else -> "What can I do for you?"
                }
            }

            _state.update {
                it.copy(
                    messages = listOf(ChatMessage(greeting, false))
                )
            }
        }
    }
}