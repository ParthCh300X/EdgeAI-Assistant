package parth.appdev.edgeaiassistant.engine.context

import parth.appdev.edgeaiassistant.domain.intent.IntentType

class ContextManager {

    private var context = ContextState()

    fun update(intent: IntentType, input: String) {
        context = ContextState(
            lastIntent = intent,
            lastInput = input,
            timestamp = System.currentTimeMillis()
        )
    }

    fun get(): ContextState = context

    fun clear() {
        context = ContextState() // ✅ reset properly
    }

    fun isValid(): Boolean {
        val diff = System.currentTimeMillis() - context.timestamp
        return diff < 60_000 // 60 sec window
    }
    fun update(intent: IntentType, input: String, pending: Boolean = false){
        context = ContextState(
            lastIntent = intent,
            lastInput = input,
            timestamp = System.currentTimeMillis(),
            isPending = pending
        )
    }
}