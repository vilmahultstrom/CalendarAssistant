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

    private val _firstEventWithLocation = MutableStateFlow<CalendarEvent?>(null)
    val firstEventWithLocation = _firstEventWithLocation.asStateFlow()



    fun setCalendarList(calendarList: List<Calendar>){
        _calendarList.value = calendarList
    }


    fun getFirstEventWithLocation() {
        val events = mutableListOf<CalendarEvent>()
        for (calendar in _calendarList.value) {
            for (event in calendar.calendarEvents) {
                if (event.location != null) {
                    events.add(event)
                }
            }
        }





    }




}



