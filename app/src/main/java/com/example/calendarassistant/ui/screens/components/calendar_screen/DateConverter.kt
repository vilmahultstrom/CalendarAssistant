package com.example.calendarassistant.ui.screens.components.calendar_screen

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * converts from seconds since Unix epoch (January 1, 1970, 00:00:00 GMT) to format of pattern "HH:mm yyyy-MM-dd"
 */
fun DateConverter(time:Long):String {
    val instant = Instant.ofEpochSecond(time)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    val formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd")
    val formattedTime = localDateTime.format(formatter)
    return formattedTime
}