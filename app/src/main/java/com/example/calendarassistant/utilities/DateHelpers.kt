package com.example.calendarassistant.utilities

import java.time.LocalDate
import java.time.YearMonth

fun getCurrentMonthDates(date: LocalDate): List<String> {
    val yearMonth = YearMonth.of(date.year, date.month)
    val daysInMonth = yearMonth.lengthOfMonth()

    return (1..daysInMonth).map { it.toString() }
}
