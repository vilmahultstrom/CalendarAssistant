package com.example.calendarassistant.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.login.SignInInterface
import com.example.calendarassistant.model.calendar.Calendars
import com.example.calendarassistant.model.mock.calendar.MockCalendarEvent
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.model.travel.DeviationInformation
import com.example.calendarassistant.model.travel.TravelInformation
import com.example.calendarassistant.model.travel.TransitDeviationData
import com.example.calendarassistant.model.travel.TravelInformationData
import com.example.calendarassistant.network.GoogleApi
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.network.location.LocationRepository
import com.example.calendarassistant.network.location.LocationService
import com.example.calendarassistant.services.NetworkService
import com.example.calendarassistant.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TestVm"


@HiltViewModel
class TestVM @Inject constructor(
    private val networkService: NetworkService
) : ViewModel() {

    private var signInAttemptListener: SignInInterface? = null
    fun setSignInAttemptListener(listener: SignInInterface) {
        signInAttemptListener = listener
    }

    private var fetchEventsGoogleListener: SignInInterface? = null
    fun setFetchEventsGoogleListener(listener: SignInInterface) {
        fetchEventsGoogleListener = listener
    }
    private var isFetchingLocationData: Boolean = false


    private val _startServiceAction = mutableStateOf<Event<String>?>(null)
    val startServiceAction: State<Event<String>?> = _startServiceAction

    private val _mockEvents = MutableStateFlow(MockEvent.getMockEventsFormattedConvertedTime()) //TODO: this data should come from Google Calendar api
    val mockEvents: StateFlow<List<MockCalendarEvent>> = _mockEvents

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

    fun login() {
        viewModelScope.launch {
            signInAttemptListener?.attemptSignIn()
            //signin.attemptSignIn()
            Log.d(TAG, "loggin in button pressed")
        }
    }

    fun fetchEvents() {
        viewModelScope.launch {
            fetchEventsGoogleListener?.fetchEvents()
            //signin.attemptSignIn()
            Log.d(TAG, "loggin in button pressed")
        }
    }

    // Start fetching gps data
    fun onStartServiceClicked() {
        MockEvent.getMockEventsFormattedConvertedTime()
        if (!isFetchingLocationData) {
            startLocationService()
        } else {
            stopLocationService()
        }
    }

    private fun startLocationService() {
        _startServiceAction.value = Event(LocationService.ACTION_START)
        fetchTravelInformation()
        isFetchingLocationData = true
    }

    private fun stopLocationService() {
        _startServiceAction.value = Event(LocationService.ACTION_STOP)
        clearLocationData()
        isFetchingLocationData = false
    }

    private fun clearLocationData() {
        _uiState.update { it.copy(currentLatitude = "", currentLongitude = "") }
    }

    private fun fetchTravelInformation() {
        viewModelScope.launch {
            networkService.getTravelInformation(TravelMode.Transit, Calendars.firstEventWithLocation.value) // TODO: ändra till valda TravelMode
        }
    }

    /*fun setTravelMode(mode: TravelMode) {
        _uiState.update { _uiState.value.copy(travelMode = mode) }
        viewModelScope.launch {
            networkService.getTravelInformation(mode, it.startDateTime)
        }
    }
*/


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
                    Event(LocationService.ACTION_GET) // Inits and collects location info
                delay(10000)    // Delay for init
                networkService.getTravelInformation(_uiState.value.travelMode, Calendars.firstEventWithLocation.value) // fetches data
                networkService.getDeviationInformation()
            }

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
                    networkService.getTravelInformation(_uiState.value.travelMode, Calendars.firstEventWithLocation.value)
                    networkService.getDeviationInformation()
                }
            }

            // Coroutine for collecting next mock event for display
            launch {
                TravelInformation.getNextEventTravelInformation().collect { next: TravelInformationData ->
                    Log.d(TAG, "Collecting: $next")
                    _uiState.update { currentState -> currentState.copy(travelInformationData = next) }
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
