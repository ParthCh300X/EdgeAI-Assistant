package parth.appdev.edgeaiassistant.domain.dispatcher

import android.content.Context
import parth.appdev.edgeaiassistant.domain.command.CalculatorCommand
import parth.appdev.edgeaiassistant.domain.command.Command
import parth.appdev.edgeaiassistant.domain.command.GetNotesCommand
import parth.appdev.edgeaiassistant.domain.command.OpenAppCommand
import parth.appdev.edgeaiassistant.domain.command.SaveNoteCommand
import parth.appdev.edgeaiassistant.domain.command.SetAlarmCommand
import parth.appdev.edgeaiassistant.domain.command.UnitConvertCommand
import parth.appdev.edgeaiassistant.domain.intent.IntentType
import parth.appdev.edgeaiassistant.engine.slots.CalculatorSlotExtractor
import parth.appdev.edgeaiassistant.engine.slots.ReminderSlotExtractor

class CommandDispatcher(
    private val context: Context
) {

    fun dispatch(intent: IntentType, input: String): Command {

        return when (intent) {

            IntentType.CALCULATE -> {
                val extractor = CalculatorSlotExtractor()
                val slots = extractor.extract(input)
                CalculatorCommand(input)
            }

            IntentType.CONVERT_UNITS -> UnitConvertCommand(input)
            IntentType.TAKE_NOTE -> SaveNoteCommand(context, input)
            IntentType.GET_NOTES -> GetNotesCommand(context)
            IntentType.OPEN_APP -> OpenAppCommand(context, input)
            IntentType.SET_ALARM -> SetAlarmCommand(context, input)

            else -> DummyCommand("Didn't understand")
        }
    }
}

class DummyCommand(
    private val message: String
) : Command {
    override fun execute(): String = message
}