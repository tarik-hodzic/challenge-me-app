package com.example.challengeme

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class StopwatchService : Service() {

    private var seconds = 0
    private var isRunning = true
    private var job: Job? = null
    private val channelId = "stopwatch_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        when (intent?.action) {
            "START" -> startStopwatch()
            "STOP" -> stopSelf()
        }

        return START_STICKY
    }

    private fun startStopwatch() {
        val notification = buildNotification("Stopwatch started")
        startForeground(1, notification)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(1000)
                seconds++
                updateNotification("Running: $seconds sec")

                val intent = Intent("STOPWATCH_TICK")
                intent.putExtra("seconds", seconds)
                sendBroadcast(intent)

            }
        }
    }

    private fun updateNotification(content: String) {
        val notification = buildNotification(content)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

    private fun buildNotification(text: String): Notification {
        val stopIntent = Intent(this, StopwatchActionReceiver::class.java).apply {
            action = "STOP"
        }
        val pendingStopIntent = PendingIntent.getBroadcast(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Challenge Timer")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .addAction(0, "Stop", pendingStopIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Stopwatch Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        job?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
