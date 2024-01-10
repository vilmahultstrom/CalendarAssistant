package com.example.calendarassistant.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.model.calendar.Calendar
import com.example.calendarassistant.model.calendar.CalendarEvent
import com.example.calendarassistant.model.calendar.Calendars
import com.example.calendarassistant.services.CalendarService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "CalendarVM"

@HiltViewModel
class CalendarVM @Inject constructor(private val calendarService: CalendarService
): ViewModel() {

    private val _eventsWithLocation = MutableStateFlow<List<CalendarEvent>>(listOf()) //events
    val eventsWithLocation = _eventsWithLocation.asStateFlow()

    private val _calendars = MutableStateFlow<List<Calendar>>(listOf()) //calendars
    val calendars = _calendars.asStateFlow()

    // which day, month, year was selected on screen:
    private val _selectedMonthIndex = MutableStateFlow(LocalDate.now().monthValue)
    private val _selectedDayIndex = MutableStateFlow(LocalDate.now().dayOfMonth) // Note: monthValue is 1-12 for January-December
    private val _selectedYearIndex = MutableStateFlow(LocalDate.now().year)

    val selectedMonthIndex: StateFlow<Int> = _selectedMonthIndex
    val selectedDayIndex: StateFlow<Int> = _selectedDayIndex
    val selectedYearIndex: StateFlow<Int> = _selectedYearIndex

    // Methods to update the selected values
    fun updateSelectedMonthIndex(newIndex: Int) {
        _selectedMonthIndex.value = newIndex
        fetchEventsForSelectedDay()
        Log.d(TAG, newIndex.toString())
    }

    fun updateSelectedDayIndex(newIndex: Int) {
        _selectedDayIndex.value = newIndex +1
        fetchEventsForSelectedDay()
        Log.d(TAG, newIndex.toString())
    }

    fun updateSelectedYearIndex(newIndex: Int) {
        _selectedYearIndex.value = newIndex
        fetchEventsForSelectedDay()
        Log.d(TAG, newIndex.toString())
    }


    /**
     * hämtar alla events för den dagen som har blivit vald på skärmen
     */
    private fun fetchEventsForSelectedDay(){
        val date: LocalDate = LocalDate.of(_selectedYearIndex.value, _selectedMonthIndex.value, _selectedDayIndex.value)
        calendarService.getUpcomingEventsForOneDay(date)
    }

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