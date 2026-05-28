package parth.appdev.edgeaiassistant.engine.ml

import parth.appdev.edgeaiassistant.domain.intent.IntentType

object IntentMapper {

    // Indices match alphabetical label order from LabelEncoder in training:
    // 0=CALCULATE, 1=CONVERT_UNITS, 2=GENERAL, 3=GET_NOTES,
    // 4=OPEN_APP,  5=SET_ALARM,     6=TAKE_NOTE
    fun map(index: Int): IntentType = when (index) {
        0    -> IntentType.CALCULATE
        1    -> IntentType.CONVERT_UNITS
        2    -> IntentType.GENERAL
        3    -> IntentType.GET_NOTES
        4    -> IntentType.OPEN_APP
        5    -> IntentType.SET_ALARM
        6    -> IntentType.TAKE_NOTE
        else -> IntentType.GENERAL
    }
}