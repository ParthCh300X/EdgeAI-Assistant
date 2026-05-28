package parth.appdev.edgeaiassistant.engine.slots

import java.util.Calendar

class TimeParser {

    fun parseTime(input: String): Long? {
        val text = input.lowercase().trim()
        val now  = Calendar.getInstance()

        // ── "in X minutes/hours" ─────────────────────────────────────────
        Regex("in\\s+(\\d+)\\s*minute").find(text)?.let {
            val mins = it.groupValues[1].toLongOrNull() ?: return@let
            return now.timeInMillis + mins * 60_000L
        }

        Regex("in\\s+(\\d+)\\s*hour").find(text)?.let {
            val hrs = it.groupValues[1].toLongOrNull() ?: return@let
            return now.timeInMillis + hrs * 3_600_000L
        }

        Regex("after\\s+(\\d+)\\s*minute").find(text)?.let {
            val mins = it.groupValues[1].toLongOrNull() ?: return@let
            return now.timeInMillis + mins * 60_000L
        }

        Regex("after\\s+(\\d+)\\s*hour").find(text)?.let {
            val hrs = it.groupValues[1].toLongOrNull() ?: return@let
            return now.timeInMillis + hrs * 3_600_000L
        }

        // ── Named times ──────────────────────────────────────────────────
        when {
            text.contains("noon") || text.contains("12 pm") ->
                return buildTime(12, 0, now)

            text.contains("midnight") || text.contains("12 am") ->
                return buildTime(0, 0, now)

            text.contains("morning") && !text.contains("\\d".toRegex()) ->
                return buildTime(8, 0, now)

            text.contains("afternoon") && !text.contains("\\d".toRegex()) ->
                return buildTime(14, 0, now)

            text.contains("evening") && !text.contains("\\d".toRegex()) ->
                return buildTime(18, 0, now)

            text.contains("night") && !text.contains("\\d".toRegex()) ->
                return buildTime(21, 0, now)
        }

        // ── "half past X" ────────────────────────────────────────────────
        Regex("half\\s+past\\s+(\\d{1,2})").find(text)?.let {
            val h = it.groupValues[1].toIntOrNull() ?: return@let
            return buildTimeWithAmPm(h, 30, text, now)
        }

        // ── "quarter past X" ─────────────────────────────────────────────
        Regex("quarter\\s+past\\s+(\\d{1,2})").find(text)?.let {
            val h = it.groupValues[1].toIntOrNull() ?: return@let
            return buildTimeWithAmPm(h, 15, text, now)
        }

        // ── "quarter to X" ───────────────────────────────────────────────
        Regex("quarter\\s+to\\s+(\\d{1,2})").find(text)?.let {
            val h = it.groupValues[1].toIntOrNull() ?: return@let
            val adjustedH = if (h == 0) 23 else h - 1
            return buildTimeWithAmPm(adjustedH, 45, text, now)
        }

        // ── "X:MM am/pm" or "X.MM am/pm" ────────────────────────────────
        Regex("(\\d{1,2})[:.](\\d{2})\\s*(am|pm)?").find(text)?.let {
            val h    = it.groupValues[1].toIntOrNull() ?: return@let
            val m    = it.groupValues[2].toIntOrNull() ?: return@let
            val ampm = it.groupValues[3].ifBlank { null }
            return buildTimeExplicit(h, m, ampm, now)
        }

        // ── "X am/pm" ────────────────────────────────────────────────────
        Regex("(\\d{1,2})\\s*(am|pm)").find(text)?.let {
            val h    = it.groupValues[1].toIntOrNull() ?: return@let
            val ampm = it.groupValues[2]
            return buildTimeExplicit(h, 0, ampm, now)
        }

        // ── "tomorrow" with or without time ─────────────────────────────
        if (text.contains("tomorrow")) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 1)
            cal.set(Calendar.HOUR_OF_DAY, 9)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        // ── Plain number — assume AM if <7, PM if >=1 ────────────────────
        Regex("\\b(\\d{1,2})\\b").find(text)?.let {
            val h = it.groupValues[1].toIntOrNull() ?: return@let
            if (h in 0..23) {
                // Heuristic: 1-6 alone likely means PM, 7-12 likely AM
                val guessedHour = when {
                    h in 1..6   -> h + 12   // 1→13, 6→18
                    else        -> h
                }
                return buildTime(guessedHour, 0, now)
            }
        }

        return null
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun buildTime(hour: Int, minute: Int, reference: Calendar): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (cal.timeInMillis <= reference.timeInMillis) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    private fun buildTimeExplicit(
        hour   : Int,
        minute : Int,
        ampm   : String?,
        now    : Calendar
    ): Long {
        var h = hour
        when (ampm) {
            "pm" -> if (h != 12) h += 12
            "am" -> if (h == 12) h = 0
            null -> {
                // No am/pm — use heuristic
                if (h in 1..6) h += 12
            }
        }
        return buildTime(h, minute, now)
    }

    private fun buildTimeWithAmPm(
        hour    : Int,
        minute  : Int,
        fullText: String,
        now     : Calendar
    ): Long {
        var h = hour
        val hasPm = fullText.contains("pm") || fullText.contains("evening") || fullText.contains("night")
        val hasAm = fullText.contains("am") || fullText.contains("morning")
        when {
            hasPm && h != 12 -> h += 12
            hasAm && h == 12 -> h = 0
            h in 1..6        -> h += 12   // default heuristic
        }
        return buildTime(h, minute, now)
    }
}