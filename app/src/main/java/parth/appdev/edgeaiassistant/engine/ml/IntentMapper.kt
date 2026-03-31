package parth.appdev.edgeaiassistant.engine.ml

import parth.appdev.edgeaiassistant.domain.intent.IntentType

object IntentMapper {

    fun map(index: Int): IntentType {
        return when (index) {
            0 -> IntentType.SET_ALARM
            1 -> IntentType.CALCULATE
            2 -> IntentType.CONVERT_UNITS
            3 -> IntentType.TAKE_NOTE
            4 -> IntentType.OPEN_APP
            else -> IntentType.GENERAL
        }
    }
}