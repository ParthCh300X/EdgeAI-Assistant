package parth.appdev.edgeaiassistant.features.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    private var countDownTimer: CountDownTimer? = null
    private val channelId = "timer_channel"
    private val notifId   = 2001

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val durationMs = intent?.getLongExtra("duration_ms", 0L) ?: 0L
        val label      = intent?.getStringExtra("label") ?: "Timer"

        // MUST call startForeground immediately — within 5 seconds of start
        createChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                notifId,
                buildNotification("$label starting..."),
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(notifId, buildNotification("$label starting..."))
        }
        if (durationMs <= 0L) {
            stopSelf()
            return START_NOT_STICKY
        }

        countDownTimer = object : CountDownTimer(durationMs, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val s  = millisUntilFinished / 1000
                val mm = s / 60
                val ss = s % 60
                updateNotification("$label — %02d:%02d left".format(mm, ss))
            }

            override fun onFinish() {
                updateNotification("$label — Done! ✓")
                stopForeground(false)
                stopSelf()
            }
        }.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timers",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Edge AI countdown timers" }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String) =
        NotificationCompat.Builder(this, channelId)
            .setContentTitle("Edge AI Timer")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setSilent(true)
            .build()

    private fun updateNotification(text: String) {
        getSystemService(NotificationManager::class.java).notify(notifId, buildNotification(text))
    }
}