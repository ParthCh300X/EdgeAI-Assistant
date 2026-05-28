package parth.appdev.edgeaiassistant.domain.command

import android.content.Context
import android.content.Intent
import android.os.Build
import parth.appdev.edgeaiassistant.features.timer.TimerService

class TimerCommand(
    private val context: Context,
    private val input: String
) : Command {

    override suspend fun execute(): String {
        val parsed = parseDuration(input)
            ?: return "Couldn't understand the duration. Try: \"set a timer for 5 minutes\""

        val (durationMs, label) = parsed

        val intent = Intent(context, TimerService::class.java).apply {
            putExtra("duration_ms", durationMs)
            putExtra("label", label)
        }

        // FIXED: must use startForegroundService on Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        val totalSeconds = durationMs / 1000
        return if (totalSeconds >= 60) {
            "Timer set for ${totalSeconds / 60} min ${totalSeconds % 60} sec ✓"
        } else {
            "Timer set for $totalSeconds seconds ✓"
        }
    }

    private fun parseDuration(input: String): Pair<Long, String>? {
        val text = input.lowercase()
        var totalMs = 0L

        val hourMatch   = Regex("(\\d+)\\s*hour").find(text)
        val minuteMatch = Regex("(\\d+)\\s*(minute|min)").find(text)
        val secondMatch = Regex("(\\d+)\\s*(second|sec)").find(text)

        hourMatch?.groupValues?.get(1)?.toLongOrNull()?.let   { totalMs += it * 3_600_000L }
        minuteMatch?.groupValues?.get(1)?.toLongOrNull()?.let { totalMs += it * 60_000L }
        secondMatch?.groupValues?.get(1)?.toLongOrNull()?.let { totalMs += it * 1_000L }

        if (totalMs <= 0L) return null

        val label = text
            .replace(Regex("(set\\s+a?\\s*timer\\s*(for)?)"), "")
            .replace(Regex("\\d+\\s*(hour|minute|min|second|sec)s?"), "")
            .trim()
            .ifBlank { "Timer" }
            .replaceFirstChar { it.uppercase() }

        return Pair(totalMs, label)
    }
}