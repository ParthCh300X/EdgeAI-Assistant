package parth.appdev.edgeaiassistant.domain.command

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import parth.appdev.edgeaiassistant.data.local.AppDatabase
import parth.appdev.edgeaiassistant.data.local.entity.NoteEntity

class SaveNoteCommand(
    private val context: Context,
    private val input: String
) : Command {

    override fun execute(): String {

        val cleaned = input
            .trim()
            .replace(
                Regex(
                    "^(note|write|save|jot)\\s+",
                    RegexOption.IGNORE_CASE
                ),
                ""
            )
            .trim()

        if (cleaned.isBlank()) return "Nothing to save"

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getInstance(context)
                .noteDao()
                .insert(NoteEntity(content = cleaned))
        }

        return "Saved: $cleaned"
    }
}