package com.example.calendarassistant.model.alarm

object AlarmOffset {
    var alarmOffsetInMinutes: Long = 0

    fun setAlarmOffset(minutes: Long) {
        alarmOffsetInMinutes = minutes
    }
}