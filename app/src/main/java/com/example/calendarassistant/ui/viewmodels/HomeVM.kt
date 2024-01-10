package com.example.calendarassistant.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.enums.TravelMode
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
import com.example.calendarassistant.services.NetworkService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val networkService: NetworkService
): ViewModel()  {

    private val _eventsWithLocation = MutableStateFlow<List<CalendarEvent>>(listOf())
    val eventsWithLocation = _eventsWithLocation.asStateFlow()

    private val _calendars = MutableStateFlow<List<Calendar>>(listOf())
    val calendars = _calendars.asStateFlow()


    private var isFetchingLocationData: Boolean = false


    private val _startServiceAction = mutableStateOf<com.example.calendarassistant.utilities.Event<String>?>(null)
    val startServiceAction: State<com.example.calendarassistant.utilities.Event<String>?> = _startServiceAction

    private val _uiState = MutableStateFlow(
        UiState(
            travelInformation = TravelInformation(),
            transitDeviationInformation = TransitDeviationInformation()
        )
    )
    val uiState: StateFlow<UiState> = _uiState
//    val uiState: StateFlow<UiState> // TODO: vilken variant? Detta är mer robust, men speler ej så stor roll...
//        get() = _uiState.asStateFlow()

    val transitSteps: StateFlow<List<Steps>> = MockTravelInformation.transitSteps


    // Start fetching gps data
    fun onStartServiceClicked() {
        calendarService.getUpcomingEvents()
        if (!isFetchingLocationData) {
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

    private fun List<CalendarEvent>.sortedByStartTime(): List<CalendarEvent> {
        return this.sortedWith(compareBy { it.startDateTime ?: it.startDate })
    }


    private fun startLocationService() {
        _startServiceAction.value =
            com.example.calendarassistant.utilities.Event(LocationService.ACTION_START)
        fetchTravelInformation()
        isFetchingLocationData = true
    }

    private fun stopLocationService() {
        _startServiceAction.value =
            com.example.calendarassistant.utilities.Event(LocationService.ACTION_STOP)
        clearLocationData()
        isFetchingLocationData = false
    }


    private fun fetchTravelInformation() {
        viewModelScope.launch {
            networkService.getTravelInformation(TravelMode.Transit) // TODO: ändra till valda TravelMode
        }
    }

    private fun clearLocationData() {
        _uiState.update { it.copy(currentLatitude = "", currentLongitude = "") }
    }

    fun setTravelMode(mode: TravelMode) {
        _uiState.update { _uiState.value.copy(travelMode = mode) }
        viewModelScope.launch {
            networkService.getTravelInformation(mode)
        }
    }



    fun getDirectionsByPlace() {
        viewModelScope.launch {
            val response =
                GoogleApi.getDirectionsByPlace("Nyköping", "Stockholm", TravelMode.Transit)
            Log.d(TAG, response.body().toString())
        }
    }

    fun getDirectionsByCoordinates() {
        viewModelScope.launch {
            val response = GoogleApi.getDirectionsByCoordinates(
                Pair(58.75311F, 17.009333F), Pair(59.33459f, 18.063240f), TravelMode.Transit
            )
        }
    }


    init {
        viewModelScope.launch {
            // Coroutine for getting location at start up
            launch {
                _startServiceAction.value =
                    com.example.calendarassistant.utilities.Event(LocationService.ACTION_GET) // Inits and collects location info
                delay(10000)    // Delay for init
                networkService.getTravelInformation(_uiState.value.travelMode) // fetches data
                networkService.getDeviationInformation()
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
                    networkService.getTravelInformation(_uiState.value.travelMode)
                }
            }

            // Coroutine for collecting next mock event for display
            launch {
                MockTravelInformation.getNextEventInformation().collect { next: TravelInformation ->
                    Log.d(TAG, "Collecting: $next")
                    _uiState.update { currentState -> currentState.copy(travelInformation = next) }
                }
            }
            launch {
                Calendars.calendarList.collect {
                    _calendars.value = it
                    getAllEventsWithLocationFromCalendars()
                }
            }

            launch {
                MockDeviationInformation.getNextTransitDeviationsInformation()
                    .collect { next: TransitDeviationInformation ->
                        Log.d(TAG, "Collecting: $next")
                        _uiState.update { currentState ->
                            currentState.copy(
                                transitDeviationInformation = next
                            )
                        }
                    }
            }
        }
    }
}
