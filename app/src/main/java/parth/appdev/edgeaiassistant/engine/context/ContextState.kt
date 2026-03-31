package parth.appdev.edgeaiassistant.engine.context

import parth.appdev.edgeaiassistant.domain.intent.IntentType

data class ContextState(
    val lastIntent: IntentType? = null,
    val lastInput: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isPending: Boolean = false
)