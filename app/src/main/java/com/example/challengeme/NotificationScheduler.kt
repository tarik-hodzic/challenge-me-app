package com.example.challengeme

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.util.Calendar

object NotificationScheduler {
    fun scheduleRepeatingNotification(context: Context, intervalStr: String) {
        val intervalMillis = when (intervalStr) {
            "5s" -> 5 * 1000L
            "1h" -> 1 * 60 * 60 * 1000L
            "2h" -> 2 * 60 * 60 * 1000L
            "4h" -> 4 * 60 * 60 * 1000L
            "6h" -> 6 * 60 * 60 * 1000L
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) return
        }

        if (intervalStr == "5s") {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + intervalMillis,
                pendingIntent
            )
            Toast.makeText(context, "Scheduled alarm for 5s", Toast.LENGTH_SHORT).show()
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + intervalMillis,
                intervalMillis,
                pendingIntent
            )
        }
    }

    fun scheduleDailyChallengeNotifications(context: Context) {
        scheduleExactDaily(context, 12, 0, 101)
        scheduleExactDaily(context, 21, 0, 102)
    }

    fun scheduleMiddayNotification(context: Context) {
        scheduleExactDaily(context, 12, 0, 201)
    }

    fun scheduleNightNotification(context: Context) {
        scheduleExactDaily(context, 21, 0, 202)
    }

    private fun scheduleExactDaily(context: Context, hour: Int, minute: Int, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("code", requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
