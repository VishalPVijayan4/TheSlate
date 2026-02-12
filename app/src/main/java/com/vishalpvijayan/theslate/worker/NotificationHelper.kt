package com.vishalpvijayan.theslate.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.vishalpvijayan.theslate.R

object NotificationHelper {
    private const val ALARM_CHANNEL = "alarm_channel"

    fun showAlarmNotification(context: Context, title: String, description: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ALARM_CHANNEL, "Alarms", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Alarm: $title")
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        manager.notify((System.currentTimeMillis() % 100000).toInt(), notification)
    }
}
