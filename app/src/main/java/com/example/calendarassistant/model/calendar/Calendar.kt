package com.example.calendarassistant.model.calendar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime

data class Calendar(
    val calendarInformation: CalendarInformation,
    val calendarEvents: List<CalendarEvent>
)

data class CalendarInformation(
    val summary: String?,
    val id: String?,
    val timeZone: String?,
    val backgroundColor: String?,
    val description: String?,
)

/**
 *  Datetime and date stored in unix time
 */

data class CalendarEvent (
    val startDateTime: Long?,
    val startDate: Long?,
    val endDate: Long?,
    val endDateTime: Long?,
    val location: String?,
    val summary: String?
)

object Calendars {
    private val _calendarList = MutableStateFlow<List<Calendar>>(listOf())
    val calendarList = _calendarList.asStateFlow()

    fun setCalendarList(calendarList: List<Calendar>){
        _calendarList.value = calendarList
    }

}



