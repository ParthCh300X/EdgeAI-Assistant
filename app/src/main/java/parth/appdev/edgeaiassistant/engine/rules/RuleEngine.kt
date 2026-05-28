package parth.appdev.edgeaiassistant.engine.rules

import parth.appdev.edgeaiassistant.domain.intent.IntentType
import parth.appdev.edgeaiassistant.domain.model.IntentResult

class RuleEngine {

    fun detectIntent(input: String): IntentResult {

        val text = input.lowercase().trim()

        return when {

            // CALCULATOR
            text.contains(Regex("\\d+[+\\-*/]\\d+")) ||
                    text.contains("calculate") ||
                    text.contains("solve") ->
                IntentResult(IntentType.CALCULATE, 0.95f)

            // UNIT CONVERTER
            text.contains("convert") ||
                    (text.contains(" in ") && (
                            text.contains("kg") || text.contains("gram") ||
                                    text.contains("km") || text.contains("meter") ||
                                    text.contains("celsius") || text.contains("fahrenheit") ||
                                    text.contains("miles") || text.contains("feet") ||
                                    text.contains("pounds") || text.contains("mb") || text.contains("gb")
                            )) ->
                IntentResult(IntentType.CONVERT_UNITS, 0.9f)

            // GET NOTES (priority above TAKE_NOTE)
            text.contains("my notes") ||
                    text.contains("view notes") ||
                    text.contains("all notes") ||
                    text.contains("what did i write") ||
                    text.contains("what i saved") ||
                    text.contains("show notes") ||
                    text.contains("list notes") ->
                IntentResult(IntentType.GET_NOTES, 0.95f)

            // WEATHER
            text.contains("weather") ||
                    text.contains("temperature") ||
                    text.contains("rain") ||
                    text.contains("sunny") ||
                    text.contains("forecast") ||
                    text.contains("how hot") ||
                    text.contains("how cold") ->
                IntentResult(IntentType.WEATHER, 0.9f)

            // TIMER
            text.contains("timer") ||
                    (text.contains("set") && text.contains("minute")) ||
                    (text.contains("set") && text.contains("second")) ||
                    text.contains("countdown") ||
                    text.contains("count down") ->
                IntentResult(IntentType.TIMER, 0.9f)

            // ALARM
            text.contains("alarm") ||
                    text.contains("wake me") ||
                    text.contains("remind me") ->
                IntentResult(IntentType.SET_ALARM, 0.9f)

            // OPEN APP
            text.contains("open") ||
                    text.contains("launch") ->
                IntentResult(IntentType.OPEN_APP, 0.9f)

            // TAKE NOTE (strict — must start with keyword)
            text.startsWith("note ") ||
                    text.startsWith("write ") ||
                    text.startsWith("save ") ||
                    text.startsWith("jot ") ->
                IntentResult(IntentType.TAKE_NOTE, 0.9f)

            else ->
                IntentResult(IntentType.GENERAL, 0.3f)
        }
    }
}