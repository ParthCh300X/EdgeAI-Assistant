package parth.appdev.edgeaiassistant.domain.dispatcher

import android.content.Context
import parth.appdev.edgeaiassistant.data.repository.NoteRepository
import parth.appdev.edgeaiassistant.domain.command.*
import parth.appdev.edgeaiassistant.domain.intent.IntentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandDispatcher @Inject constructor(
    private val context: Context,
    private val noteRepository: NoteRepository
) {

    fun dispatch(intent: IntentType, input: String): Command {
        return when (intent) {
            IntentType.CALCULATE     -> CalculatorCommand(input)
            IntentType.CONVERT_UNITS -> UnitConvertCommand(input)
            IntentType.TAKE_NOTE     -> SaveNoteCommand(noteRepository, input)
            IntentType.GET_NOTES     -> GetNotesCommand(noteRepository)
            IntentType.OPEN_APP      -> OpenAppCommand(context, input)
            IntentType.SET_ALARM     -> SetAlarmCommand(context, input)
            IntentType.WEATHER -> WeatherCommand(context)
            IntentType.TIMER   -> TimerCommand(context, input)
            else                     -> DummyCommand("I didn't understand that. Try: set alarm, calculate, convert, note, or open an app.")
        }
    }
}

class DummyCommand(private val message: String) : Command {
    override suspend fun execute(): String = message
}