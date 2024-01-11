package com.example.calendarassistant.model.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.calendarassistant.AlarmReceiver
import java.time.ZoneId
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.example.calendarassistant.model.CalendarEvent
import java.time.Instant
import java.time.LocalDateTime
import java.util.regex.Pattern

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    @SuppressLint("MissingPermission")
    override fun schedule(item: AlarmItem) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Inform the user and direct them to settings if permission is not granted
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                return
            }
        }
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", item.title)
            putExtra("EXTRA_MESSAGE", item.message)
        }

        Toast.makeText(
            context,
            "Alarm set for ${item.time.minusMinutes(AlarmOffset.alarmOffsetInMinutes)}",
            Toast.LENGTH_SHORT
        ).show()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.minusMinutes(AlarmOffset.alarmOffsetInMinutes).atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    // Example: Event starts at 14:00, sets alarm for 12:00
    fun scheduleAlarmForEvent(calendarEvent: CalendarEvent) {
        Log.d("AlarmScheduler", "startDateTime: ${calendarEvent.startDateTime}")
        val alarmTime = calendarEvent.startDateTime?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it * 1000), ZoneId.systemDefault())
        }?.minusHours(2)

        alarmTime?.let {
            val alarmTitle = "2 hours until your event: ${calendarEvent.summary ?: "Event"}"
            val alarmItem = AlarmItem(
                time = it,
                title = alarmTitle,
                message = ""
            )
            schedule(alarmItem)
        }
    }

    // Example: 6:34 AM sets alarm for 6:29 AM
    fun scheduleAlarmForEvent(timeString: String) {
        Log.d("AlarmScheduler", "Time string: $timeString")

        // Parse the time string
        val pattern = Pattern.compile("(\\d+)h(\\d+)m")
        val matcher = pattern.matcher(timeString)
        if (!matcher.matches()) {
            Log.e("AlarmScheduler", "Invalid time format")
            return
        }

        val hours = matcher.group(1)?.toInt()
        val minutes = matcher.group(2)?.toInt()

        // Calculate the alarm time
        val alarmTime = hours?.let {
            minutes?.let { it1 ->
                LocalDateTime.now(ZoneId.systemDefault())
                    .plusHours(it.toLong())
                    .plusMinutes(it1.toLong())
                    .minusMinutes(5)
            }
        }

        val alarmTitle = "Leave in 5 minutes for your next event"
        val alarmItem = alarmTime?.let {
            AlarmItem(
                time = it,
                title = alarmTitle,
                message = ""
            )
        }
        if (alarmItem != null) {
            schedule(alarmItem)
        }
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

}