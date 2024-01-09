package com.example.calendarassistant.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.calendarassistant.login.GoogleCalendar
import com.example.calendarassistant.model.mock.calendar.MockCalendarEvent
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.services.CalendarService
import com.google.api.services.calendar.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val TAG = "HomeVM"

@HiltViewModel
class HomeVM @Inject constructor(private val calendarService: CalendarService
): ViewModel()  {

    private val _events = calendarService.events
    val events: StateFlow<List<Event>> = _events

    // Start fetching gps data
    fun onStartServiceClicked() {
        calendarService.getUpcomingEvents()
    }



}