package com.example.calendarassistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.calendarassistant.ui.viewmodels.SettingsVM
import com.example.calendarassistant.utilities.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    // This code will execute when alarm triggers
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("EXTRA_TITLE") ?: return
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: return
        Log.d("ALARM", "Title: $title, Message: $message")
        notificationHelper.showNotification(title = title, contentText = message)
    }

}