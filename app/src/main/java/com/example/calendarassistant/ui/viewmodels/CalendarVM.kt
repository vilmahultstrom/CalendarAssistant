package com.example.calendarassistant.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.login.GoogleCalendar
import com.example.calendarassistant.login.SignInInterface
import com.example.calendarassistant.model.calendar.Calendar
import com.example.calendarassistant.model.calendar.CalendarEvent
import com.example.calendarassistant.model.calendar.Calendars
import com.example.calendarassistant.model.mock.calendar.MockCalendarEvent
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.model.mock.travel.MockDeviationInformation
import com.example.calendarassistant.model.mock.travel.MockTravelInformation
import com.example.calendarassistant.model.mock.travel.TransitDeviationInformation
import com.example.calendarassistant.model.mock.travel.TravelInformation
import com.example.calendarassistant.network.GoogleApi
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.network.location.LocationRepository
import com.example.calendarassistant.network.location.LocationService
import com.example.calendarassistant.services.CalendarService
import com.example.calendarassistant.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CalendarVM"

@HiltViewModel
class CalendarVM @Inject constructor(private val calendarService: CalendarService
): ViewModel() {

    private val _eventsWithLocation = MutableStateFlow<List<CalendarEvent>>(listOf())
    val eventsWithLocation = _eventsWithLocation.asStateFlow()

    private val _calendars = MutableStateFlow<List<Calendar>>(listOf())
    val calendars = _calendars.asStateFlow()

    /**
     *  Testar att få in alla events med platsdata
     */
    private fun getAllEventsWithLocationFromCalendars() {
        var totalEvents = 0
        val events = mutableListOf<CalendarEvent>()
        for(calendar in calendars.value) {
            for (event in calendar.calendarEvents){
                totalEvents++
                if (event.location != null) {
                    events.add(event)
                }
            }
        }
        Log.d(TAG, "No of events with location: " + events.size.toString())
        Log.d(TAG, "Total no of events: $totalEvents")
        _eventsWithLocation.value = events.sortedByStartTime()
    }

    /**
     * sorts a List<CalendarEvent> by startTime
     */
    private fun List<CalendarEvent>.sortedByStartTime(): List<CalendarEvent> {
        return this.sortedWith(compareBy { it.startDateTime ?: it.startDate })
    }

    init {
        viewModelScope.launch {
            // Coroutine for getting location at start up
            launch {
                calendarService.getUpcomingEvents()
            }
            launch {
                Calendars.calendarList.collect {
                    _calendars.value = it
                    getAllEventsWithLocationFromCalendars()
                }
            }
        }

    }

}