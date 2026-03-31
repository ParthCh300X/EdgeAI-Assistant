package parth.appdev.edgeaiassistant.engine.slots

import java.util.Calendar

class TimeParser {

    fun parseTime(input: String): Long? {
        val lower = input.lowercase()
        val calendar = Calendar.getInstance()

        return when {

            lower.contains("tomorrow") -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 9)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.timeInMillis
            }

            Regex("\\b\\d{1,2}\\s?(am|pm)\\b").containsMatchIn(lower) -> {
                val match = Regex("(\\d{1,2})\\s?(am|pm)").find(lower)
                val hour = match?.groupValues?.get(1)?.toIntOrNull() ?: return null
                val ampm = match.groupValues[2]

                val now = Calendar.getInstance()
                val target = Calendar.getInstance()

                var finalHour = hour
                if (ampm == "pm" && hour != 12) finalHour += 12
                if (ampm == "am" && hour == 12) finalHour = 0

                target.set(Calendar.HOUR_OF_DAY, finalHour)
                target.set(Calendar.MINUTE, 0)
                target.set(Calendar.SECOND, 0)

                // 🔥 CRITICAL FIX
                if (target.before(now)) {
                    target.add(Calendar.DAY_OF_YEAR, 1)
                }

                target.timeInMillis
            }

            else -> null
        }
    }
}