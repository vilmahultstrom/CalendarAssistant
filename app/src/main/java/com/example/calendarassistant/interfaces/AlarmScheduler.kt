package com.example.calendarassistant.interfaces

import com.example.calendarassistant.model.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}