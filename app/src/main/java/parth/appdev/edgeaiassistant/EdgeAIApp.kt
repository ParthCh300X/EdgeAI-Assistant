package parth.appdev.edgeaiassistant

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EdgeAIApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Timer channel
            manager.createNotificationChannel(
                NotificationChannel(
                    "timer_channel",
                    "Timers",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "Edge AI countdown timers" }
            )

            // Alarm channel
            manager.createNotificationChannel(
                NotificationChannel(
                    "alarm_channel",
                    "Alarms",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Edge AI alarm notifications" }
            )
        }
    }
}