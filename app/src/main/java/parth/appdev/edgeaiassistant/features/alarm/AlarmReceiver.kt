package parth.appdev.edgeaiassistant.features.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.annotation.SuppressLint

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        try {

            val action = intent.action

            // 🔴 STOP ACTION
            if (action == ACTION_STOP) {
                AlarmSoundManager.stop()
                return
            }

            val message = intent.getStringExtra("message") ?: "Alarm"
            val channelId = "alarm_channel"

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 🔥 CHANNEL
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Alarms",
                    NotificationManager.IMPORTANCE_HIGH
                )
                manager.createNotificationChannel(channel)
            }

            // 🔥 EXPLICIT STOP INTENT (IMPORTANT FIX)
            val stopIntent = Intent(context, AlarmReceiver::class.java)
            stopIntent.action = ACTION_STOP
            stopIntent.setPackage(context.packageName)

            val stopPendingIntent = PendingIntent.getBroadcast(
                context,
                1001, // unique request code
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Alarm")
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(0, "STOP", stopPendingIntent)
                .setAutoCancel(true)
                .build()

            showNotification(context, notification)

            // 🔊 SOUND
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val ringtone = RingtoneManager.getRingtone(context, alarmUri)
            AlarmSoundManager.ringtone = ringtone

            try {
                ringtone?.play()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val ACTION_STOP = "STOP_ALARM"
    }

    // 🔥 LINT-SAFE NOTIFICATION
    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, notification: android.app.Notification) {
        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}