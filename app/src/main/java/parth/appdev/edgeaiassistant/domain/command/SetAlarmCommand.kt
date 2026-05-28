package parth.appdev.edgeaiassistant.domain.command

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import parth.appdev.edgeaiassistant.features.alarm.AlarmReceiver
import java.util.Calendar

class SetAlarmCommand(
    private val context : Context,
    private val input   : String
) : Command {

    override suspend fun execute(): String {

        val parsed = parseAlarm(input)
            ?: return "Couldn't understand the time. Try: \"set alarm for 7am\" or \"wake me at 8:30pm\""

        val (timeMillis, message) = parsed

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    return "Please enable exact alarms in Settings > Special app access."
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeMillis,
                pendingIntent
            )

            // Format confirmation time for user
            val cal  = Calendar.getInstance().apply { timeInMillis = timeMillis }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val min  = cal.get(Calendar.MINUTE)
            val ampm = if (hour < 12) "AM" else "PM"
            val h12  = when {
                hour == 0  -> 12
                hour > 12  -> hour - 12
                else       -> hour
            }
            val minStr = if (min == 0) "" else ":%02d".format(min)
            "Alarm set for $h12$minStr $ampm ✓"

        } catch (e: SecurityException) {
            "Permission required to set alarm."
        }
    }

    private fun parseAlarm(input: String): Pair<Long, String>? {
        val text  = input.lowercase()
        val parser = parth.appdev.edgeaiassistant.engine.slots.TimeParser()
        val time  = parser.parseTime(text) ?: return null

        val message = text
            .replace(Regex("set\\s+alarm"), "")
            .replace(Regex("alarm\\s+for"), "")
            .replace(Regex("wake\\s+me(\\s+up)?"), "")
            .replace(Regex("remind\\s+me(\\s+to)?"), "")
            .replace(Regex("at\\s+\\d{1,2}[:.\\s]*(\\d{2})?\\s*(am|pm)?"), "")
            .replace(Regex("\\d{1,2}[:.](\\d{2})\\s*(am|pm)?"), "")
            .replace(Regex("\\d{1,2}\\s*(am|pm)"), "")
            .replace("noon", "")
            .replace("midnight", "")
            .trim()
            .ifBlank { "Alarm" }
            .replaceFirstChar { it.uppercase() }

        return Pair(time, message)
    }
}