package com.example.calendarassistant.utilities

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.calendarassistant.R
import javax.inject.Inject

class NotificationHelper @Inject constructor(private val application: Application) {

    private val notificationManager: NotificationManager =
        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(title: String, contentText: String) {
        val notification = NotificationCompat.Builder(application, "channel_id")
            .setSmallIcon(R.drawable.baseline_navigation_24)
            .setContentTitle(title)
            .setContentText(contentText)
            .build()
        notificationManager.notify(1, notification)
    }
}