package com.example.calendarassistant.model.calendar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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


    private val _firstEventWithLocation = MutableStateFlow<CalendarEvent?>(null)
    val firstEventWithLocation = _firstEventWithLocation.asStateFlow()


    fun setCalendarList(calendarList: List<Calendar>){
        _calendarList.value = calendarList
        _firstEventWithLocation.value = setFirstEventWithLocation()
    }



    private fun setFirstEventWithLocation(): CalendarEvent? {
        if (_calendarList.value.isEmpty()) return null

        val events = mutableListOf<CalendarEvent>()
        for (calendar in _calendarList.value) {
            for (event in calendar.calendarEvents) {
                if (event.location != null) {
                    events.add(event)
                }
            }
        }

        if (events.size == 1) return events.first()

        val sortedList = events.sortedByStartTime()
        return sortedList.first()
    }



    private fun List<CalendarEvent>.sortedByStartTime(): List<CalendarEvent> {
        return this.sortedWith(compareBy { it.startDateTime ?: it.startDate })
    }


    
}



