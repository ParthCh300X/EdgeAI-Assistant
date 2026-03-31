package parth.appdev.edgeaiassistant.domain.command

import android.content.Context
import kotlinx.coroutines.runBlocking
import parth.appdev.edgeaiassistant.data.local.AppDatabase

class GetNotesCommand(
    private val context: Context
) : Command {

    override fun execute(): String {

        val notes = runBlocking {
            AppDatabase.getInstance(context)
                .noteDao()
                .getAll()
        }

        if (notes.isEmpty()) return "No notes found"

        return notes.take(5).joinToString("\n") {
            "• ${it.content}"
        }
    }
}