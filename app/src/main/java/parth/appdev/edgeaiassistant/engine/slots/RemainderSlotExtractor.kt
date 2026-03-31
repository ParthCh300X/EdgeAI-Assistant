package parth.appdev.edgeaiassistant.engine.slots

import parth.appdev.edgeaiassistant.domain.model.ReminderSlots

class ReminderSlotExtractor {

    private val timeParser = TimeParser()

    fun extract(input: String): ReminderSlots? {

        val time = timeParser.parseTime(input) ?: return null

        val message = input
            .replace("remind me", "", ignoreCase = true)
            .replace(Regex("\\bat\\b.*"), "")
            .trim()

        return ReminderSlots(
            message = message.ifBlank { "Reminder" },
            timeInMillis = time
        )
    }
}