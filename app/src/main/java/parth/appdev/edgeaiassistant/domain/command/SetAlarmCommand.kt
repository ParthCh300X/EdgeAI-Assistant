package parth.appdev.edgeaiassistant.domain.command

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import parth.appdev.edgeaiassistant.features.alarm.AlarmReceiver
import java.util.*

class SetAlarmCommand(
    private val context: Context,
    private val input: String
) : Command {

    override fun execute(): String {

        val parsed = parseAlarm(input) ?: return "Couldn't understand time"

        val (timeMillis, message) = parsed

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("message", message)

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
                    return "Allow exact alarms in settings"
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeMillis,
                pendingIntent
            )

            "Alarm set"

        } catch (e: SecurityException) {
            "Permission required for alarm"
        }
    }

    // 🔥 FINAL ROBUST PARSER
    private fun parseAlarm(input: String): Pair<Long, String>? {

        val text = input.lowercase()

        // 🔥 SUPPORT BOTH ":" AND "." FORMAT
        val regex = Regex("(\\d{1,2})[:.](\\d{2})\\s*(am|pm)?|(\\d{1,2})\\s*(am|pm)")
        val match = regex.find(text) ?: return null

        var hour: Int
        var minute: Int
        var ampm: String?

        if (match.groupValues[1].isNotEmpty()) {
            // format: 1:50 or 1.50
            hour = match.groupValues[1].toInt()
            minute = match.groupValues[2].toInt()
            ampm = match.groupValues[3]
        } else {
            // format: 5 pm
            hour = match.groupValues[4].toInt()
            minute = 0
            ampm = match.groupValues[5]
        }

        // 🔥 AM/PM FIX
        if (ampm == "pm" && hour < 12) hour += 12
        if (ampm == "am" && hour == 12) hour = 0

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)

        // 🔥 FUTURE FIX (CRITICAL)
        val now = System.currentTimeMillis()
        if (cal.timeInMillis <= now) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        // 🔥 MESSAGE CLEANUP
        val message = text
            .replace(match.value, "")
            .replace("set alarm", "")
            .replace("alarm for", "")
            .replace("wake me", "")
            .replace("remind me to", "")
            .replace("at", "")
            .trim()
            .ifBlank { "Alarm" }

        return Pair(cal.timeInMillis, message)
    }
}