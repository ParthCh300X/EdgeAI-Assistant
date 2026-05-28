package parth.appdev.edgeaiassistant.domain.command

import parth.appdev.edgeaiassistant.data.repository.NoteRepository

class SaveNoteCommand(
    private val noteRepository: NoteRepository,
    private val input: String
) : Command {

    override suspend fun execute(): String {
        val cleaned = input
            .trim()
            .replace(Regex("^(note|write|save|jot)\\s+", RegexOption.IGNORE_CASE), "")
            .trim()

        if (cleaned.isBlank()) return "Nothing to save"

        noteRepository.saveNote(cleaned)
        return "Saved: $cleaned"
    }
}