package parth.appdev.edgeaiassistant.domain.command

import parth.appdev.edgeaiassistant.data.repository.NoteRepository

class GetNotesCommand(
    private val noteRepository: NoteRepository
) : Command {

    override suspend fun execute(): String {
        val notes = noteRepository.getAllNotes()
        if (notes.isEmpty()) return "No notes found"
        return notes.take(5).joinToString("\n") { "• ${it.content}" }
    }
}