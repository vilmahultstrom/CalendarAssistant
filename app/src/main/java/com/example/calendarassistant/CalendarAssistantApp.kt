package com.example.calendarassistant

import android.app.Application

class CalendarAssistantApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // För notiser vid gps
        /*
        val channel = NotificationChannel(
            "location",
            "Location",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)


         */

    }
}