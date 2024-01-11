package com.example.calendarassistant.model.alarm

import com.example.calendarassistant.model.alarm.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}