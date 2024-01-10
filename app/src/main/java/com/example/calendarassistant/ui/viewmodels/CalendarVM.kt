package com.example.calendarassistant.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.data.AndroidAlarmScheduler
import com.example.calendarassistant.login.GoogleAuthClient
import com.example.calendarassistant.login.SignInState
import com.example.calendarassistant.model.AlarmItem
import com.example.calendarassistant.model.calendar.Calendar
import com.example.calendarassistant.model.calendar.CalendarEvent
import com.example.calendarassistant.model.calendar.Calendars
import com.example.calendarassistant.services.CalendarService
import com.example.calendarassistant.utilities.DateHelpers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

private const val TAG = "CalendarVM"

@HiltViewModel
class CalendarVM @Inject constructor(
    private val calendarService: CalendarService,
    private val googleAuthClient: GoogleAuthClient,
    private val androidAlarmScheduler: AndroidAlarmScheduler
): ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventsWithLocation = MutableStateFlow<List<CalendarEvent>>(listOf()) //events
    val eventsWithLocation = _eventsWithLocation.asStateFlow()

    private val _calendars = MutableStateFlow<List<Calendar>>(listOf()) //calendars
    val calendars = _calendars.asStateFlow()

    fun setAlarm(event: CalendarEvent){
        var timeConverted = DateHelpers.convertUnixTimeToLocalDateTime(event.startDateTime!!)
        var alarmItem= AlarmItem(
            time=timeConverted,
            title="Timer set!",
            message=event.summary!!
        )
        androidAlarmScheduler.schedule(alarmItem)
    }


    // Methods to update the selected values
    fun updateSelectedMonthIndex(newIndex: Int) {

        _uiState.update { it.copy(selectedMonthIndex = newIndex) }
        fetchEventsForSelectedDay()
        Log.d(TAG, newIndex.toString())
    }

    fun isUserSignedIn(): Boolean {
        return googleAuthClient.getSignedInUser() != null
    }

    fun clearEvents() {
        _eventsWithLocation.value = listOf()
    }

    fun updateSelectedDayIndex(newIndex: Int) {
        _uiState.update { it.copy(selectedDayIndex = newIndex + 1) }
        fetchEventsForSelectedDay()
        Log.d(TAG, (newIndex + 1).toString())
    }

    fun updateSelectedYearIndex(newIndex: Int) {
        _uiState.update { it.copy(selectedYearIndex = newIndex) }
        fetchEventsForSelectedDay()
        Log.d(TAG, newIndex.toString())
    }


    /**
     * hämtar alla events för den dagen som har blivit vald på skärmen
     */
    private fun fetchEventsForSelectedDay(){
        //val date: LocalDate = LocalDate.of(_selectedYearIndex.value, _selectedMonthIndex.value, _selectedDayIndex.value)
        val date: LocalDate = LocalDate.of(_uiState.value.selectedYearIndex, uiState.value.selectedMonthIndex, uiState.value.selectedDayIndex)
        Log.d(TAG, date.toString())
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

    fun updateCalendar() {
        Calendars.setCalendarList(listOf())
        calendarService.getUpcomingEventsForOneDay(startDate = LocalDate.of(_uiState.value.selectedYearIndex, uiState.value.selectedMonthIndex, uiState.value.selectedDayIndex))
    }

    fun setStartIndex() {
        Log.d(TAG, "Updating startindex: " + (uiState.value.selectedDayIndex -1).toString())
        _uiState.update { it.copy(startIndex = (it.selectedDayIndex -1).toString()) }
    }


    init {
        viewModelScope.launch {
            // Coroutine for getting location at start up
            launch {
                Calendars.calendarList.collect {
                    _calendars.value = it
                    getAllEventsWithLocationFromCalendars()
                }
            }
        }

    }


}

data class CalendarUiState(
    val selectedDayIndex: Int = LocalDate.now().dayOfMonth,
    val dateOfToday: LocalDate = LocalDate.now(),
    val startIndex: String = (dateOfToday.dayOfMonth - 1).toString(),
    val selectedMonthIndex: Int = LocalDate.now().monthValue, // Note: monthValue is 1-12 for January-December
    val selectedYearIndex: Int = LocalDate.now().year
)