package com.example.calendarassistant.model

object AlarmOffset {
    var alarmOffsetInMinutes: Long = 0

    fun setAlarmOffset(minutes: Long) {
        alarmOffsetInMinutes = minutes
    }
}