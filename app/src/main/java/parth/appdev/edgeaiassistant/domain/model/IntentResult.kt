package parth.appdev.edgeaiassistant.domain.model

import parth.appdev.edgeaiassistant.domain.intent.IntentType

data class IntentResult(
    val intent: IntentType,
    val confidence: Float
)