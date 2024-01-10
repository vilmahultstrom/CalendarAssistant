package com.example.calendarassistant.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.data.AndroidAlarmScheduler
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.login.GoogleAuthClient
import com.example.calendarassistant.model.calendar.Calendar
import com.example.calendarassistant.model.calendar.CalendarEvent
import com.example.calendarassistant.model.calendar.Calendars
import com.example.calendarassistant.model.travel.DeviationInformation
import com.example.calendarassistant.model.travel.TravelInformation
import com.example.calendarassistant.model.travel.TransitDeviationData
import com.example.calendarassistant.model.travel.TravelInformationData
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.network.location.LocationRepository
import com.example.calendarassistant.network.location.LocationService
import com.example.calendarassistant.services.CalendarService
import com.example.calendarassistant.services.NetworkService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeVM"

@HiltViewModel
class HomeVM @Inject constructor(
    private val calendarService: CalendarService,
    private val networkService: NetworkService,
    private val googleAuthClient: GoogleAuthClient,
    private val alarmScheduler: AndroidAlarmScheduler
): ViewModel()  {

    private val _eventsWithLocation = MutableStateFlow<List<CalendarEvent>>(listOf())
    val eventsWithLocation = _eventsWithLocation.asStateFlow()

    private val _calendars = MutableStateFlow<List<Calendar>>(listOf())
    val calendars = _calendars.asStateFlow()

    private var isFetchingLocationData: Boolean = false

    private val _startServiceAction = mutableStateOf<com.example.calendarassistant.utilities.Event<String>?>(null)
    val startServiceAction: State<com.example.calendarassistant.utilities.Event<String>?> = _startServiceAction

    private val _firstEventWithLocation = MutableStateFlow<CalendarEvent?>(null)
    val firstEventWithLocation = _firstEventWithLocation.asStateFlow()

    private val _uiState = MutableStateFlow(
        UiState(
            travelInformationData = TravelInformationData(),
            transitDeviationData = TransitDeviationData()
        )
    )
    val uiState: StateFlow<UiState> = _uiState
//    val uiState: StateFlow<UiState> // TODO: vilken variant? Detta är mer robust, men speler ej så stor roll...
//        get() = _uiState.asStateFlow()

    val transitSteps: StateFlow<List<Steps>> = TravelInformation.transitSteps

    fun getUsername(): String {
        val user = googleAuthClient.getSignedInUser()
        return if(user?.username != null)
            user.username
        else
            "User"
    }

    // Start fetching gps data
    fun onStartServiceClicked() {
        calendarService.getUpcomingEventsForOneDay()
        if (!_uiState.value.isFetchingLocationData) {
            startLocationService()
        } else {
            stopLocationService()
        }
    }


    /**
     *  Testar att få in alla events med platsdata
     */

    private fun getAllEventsWithLocationFromCalendars() {
        var totalEvents = 0
        val events = mutableListOf<CalendarEvent>()
        for(calendar in calendars.value) {
            for (event in calendar.calendarEvents) {
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

    private fun List<CalendarEvent>.sortedByStartTime(): List<CalendarEvent> {
        return this.sortedWith(compareBy { it.startDateTime ?: it.startDate })
    }

    private fun startLocationService() {
        _startServiceAction.value =
            com.example.calendarassistant.utilities.Event(LocationService.ACTION_START)
        //fetchTravelInformation()
        _uiState.update { it.copy(isFetchingLocationData = true) }
    }

    private fun stopLocationService() {
        _startServiceAction.value =
            com.example.calendarassistant.utilities.Event(LocationService.ACTION_STOP)
        clearLocationData()
        _uiState.update { it.copy(isFetchingLocationData = false) }
    }


/*    private fun fetchTravelInformation() {
        viewModelScope.launch {
            networkService.getTravelInformation(_uiState.value.travelMode, it.startDateTime)
        }
    }*/

    private fun clearLocationData() {
        _uiState.update { it.copy(currentLatitude = "", currentLongitude = "") }
    }

    fun setTravelMode(mode: TravelMode) {
        _uiState.update { _uiState.value.copy(travelMode = mode) }
        viewModelScope.launch {
            networkService.getTravelInformation(mode, Calendars.firstEventWithLocation.value)
        }
    }
    private fun scheduleAlarmsForEvents(startDate: String) {
        alarmScheduler.scheduleAlarmForEvent(startDate)
    }

    private fun scheduleAlarmsForEvents(calendarEvents: List<CalendarEvent>) {
        calendarEvents.forEach { calendarEvent ->
            alarmScheduler.scheduleAlarmForEvent(calendarEvent)
        }
    }

    fun updateCalendar() {
        Log.d(TAG, "Updating Events")
       calendarService.getUpcomingEventsForOneWeek()
    }


    init {
        viewModelScope.launch {
            // Coroutine for getting location at start up
            Log.d(TAG, "Init")

            _startServiceAction.value = com.example.calendarassistant.utilities.Event(LocationService.ACTION_START) // Starts gps collection
            _uiState.update { it.copy(isFetchingLocationData = true) }
            calendarService.getUpcomingEventsForOneWeek()


            launch {
                _startServiceAction.value = com.example.calendarassistant.utilities.Event(
                    LocationService.ACTION_GET
                ) // Inits and collects location info

                Calendars.firstEventWithLocation.collect {
                    Log.d(TAG, "Fetching first event " + it.toString())
                    _firstEventWithLocation.value = it
                    networkService.getTravelInformation(_uiState.value.travelMode, it) // fetches data
                    networkService.getDeviationInformation()
                }
            }

            /**
             * TODO:
             *  Om vi inte vill anropa GPS hela tiden? Eller vill vi det?
             *  Borde vi ha ett intervall för hur mycket lon och lat ska ändras för att kolla current position igen?
             */

            // Coroutine for collecting location updates when
            launch {
                LocationRepository.getLocationUpdates().collect { location ->
                    _uiState.update {
                        _uiState.value.copy(
                            currentLatitude = location.latitude.toString(),
                            currentLongitude = location.longitude.toString()
                        )
                    }
                    // This updates the time left, could maybe ge done by internal timer
                    networkService.getTravelInformation(
                        _uiState.value.travelMode,
                        Calendars.firstEventWithLocation.value
                    )
                }
            }

            // Coroutine for collecting next mock event for display
            launch {
                TravelInformation.getNextEventTravelInformation().collect { next: TravelInformationData ->
                    Log.d(TAG, "Collecting: $next")
                    _uiState.update { currentState ->
                        currentState.copy(travelInformationData = next)
                    }

                    // Used for scheduling alarms based on next departure time
//                    Log.d("AlarmScheduler", "next.deptime: ${next.departureTime}")
//                    Log.d("AlarmScheduler", "next.deptimeHHMM: ${next.departureTimeHHMM.hhmmDisplay}")
                    //next.departureTimeHHMM.hhmmDisplay?.let { scheduleAlarmsForEvents(it) }
                }
            }

            viewModelScope.launch {
                Calendars.calendarList.collect { calendarEvents ->
                    _calendars.value = calendarEvents
                    getAllEventsWithLocationFromCalendars()

                    // Set alarm 2 hours before each event that is loaded (unused)
                   //scheduleAlarmsForEvents(_eventsWithLocation.value)
                }
            }

            launch {
                DeviationInformation.getNextTransitDeviationsInformation()
                    .collect { next: TransitDeviationData ->
                        Log.d(TAG, "Collecting: $next")
                        _uiState.update { currentState ->
                            currentState.copy(
                                transitDeviationData = next
                            )
                        }
                    }

            }
        }
    }
}

data class UiState(
    val isFetchingLocationData: Boolean = false,
    val currentLatitude: String = "",
    val currentLongitude: String = "",
    val travelInformationData: TravelInformationData,
    val transitDeviationData: TransitDeviationData,
    val travelMode: TravelMode = TravelMode.Transit
)
