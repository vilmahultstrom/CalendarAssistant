package com.example.calendarassistant.model.alarm

import java.time.LocalDate
import java.time.LocalDateTime

data class AlarmItem(
    val time: LocalDateTime,
    val title: String,
    val message: String
)
