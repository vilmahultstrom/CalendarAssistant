package com.example.calendarassistant.utilities

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


object DateHelpers{
    fun getCurrentMonthDates(date: LocalDate): List<String> {
        val yearMonth = YearMonth.of(date.year, date.month)
        val daysInMonth = yearMonth.lengthOfMonth()

        return (1..daysInMonth).map { it.toString() }
    }


    /**
     *  DateString format is "2024-01-19T09:00:00.000Z"
     *  Z indicates UTC, converts to local time
     */
    fun convertToSystemTimeZone(dateString: String): ZonedDateTime? {
        val utcDateTime = ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
        val systemTimeZone = ZoneId.systemDefault()
        return utcDateTime.withZoneSameInstant(systemTimeZone)
    }


    fun formatSecondsToHourMinutes(seconds: Long?) : String {
        if (seconds == null) return "nullValue"
        if (seconds < 0) return "Run"
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60

        return if (hours > 0) {
            "${hours}h${minutes}m"
        } else {
            "${minutes}m"
        }



    }

}

