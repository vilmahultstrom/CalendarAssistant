package com.example.calendarassistant.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.model.mock.calendar.MockCalendarEvent
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.model.mock.travel.DeviationInformation
import com.example.calendarassistant.model.mock.travel.MockDeviationInformation
import com.example.calendarassistant.model.mock.travel.MockTravelInformation
import com.example.calendarassistant.model.mock.travel.TransitDeviationInformation
import com.example.calendarassistant.model.mock.travel.TravelInformation
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TestVm"

/*
interface CalendarAssistantViewModel {
    val uiState: StateFlow<UiState>
}
*/


@HiltViewModel
class TestVM @Inject constructor(
    private val networkService: NetworkService
) : ViewModel() {

    private val _startServiceAction = mutableStateOf<Event<String>?>(null)
    val startServiceAction: State<Event<String>?> = _startServiceAction

    private val _mockEvents = MutableStateFlow(MockEvent.getMockEventsFormattedConvertedTime()) //TODO: this data should come from Google Calendar api
    val mockEvents: StateFlow<List<MockCalendarEvent>> = _mockEvents

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

    private var isFetchingLocationData: Boolean = false

    fun login() {
        viewModelScope.launch {
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

    // TODO: För test?
    fun getDirectionsByPlace() {
        viewModelScope.launch {
            val response =
                GoogleApi.getDirectionsByPlace("Nyköping", "Stockholm", TravelMode.Transit)
            Log.d(TAG, response.body().toString())
        }
    }

    // TODO: För test?
    fun getDirectionsByCoordinates() {
        viewModelScope.launch {
            val response = GoogleApi.getDirectionsByCoordinates(
                Pair(58.75311F, 17.009333F), Pair(59.33459f, 18.063240f), TravelMode.Transit
            )
        }
    }

    fun getDeviationInformation() {
        /*viewModelScope.launch {
            MockDeviationInformation.getNextTransitDeviationsInformation().collect { next: TransitDeviationInformation ->
                Log.d(TAG, "Collecting: $next")
                _uiState.update { currentState -> currentState.copy(transitDeviationInformation = next) }
            }
        }*/
        Log.d(TAG, "hejdå ******************************************")
    }


    init {
        viewModelScope.launch {

            Log.d(TAG, "1")
            // Coroutine for getting location at start up
            launch {
                _startServiceAction.value =
                    Event(LocationService.ACTION_GET) // Inits and collects location info
                delay(10000)    // Delay for init
                Log.d(TAG, "2" + _uiState.value.currentLatitude)
                networkService.getTravelInformation(_uiState.value.travelMode) // fetches data
                Log.d(TAG, "3")
            }

            /**
             * TODO (fundering) Vilma: Använda interna acceleratorn och att när den har
             *  förändrats innom ett visst intervall så ska man anropa current location.
             *  Men även var 30 minut.
             *
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
                    // TODO: om deviation ska uppdateras automatiskt, anropas det här om
                    //  det är mindre än en timme till avresa.
                    //  Annars anropas det triggat av knapptryck.

                }
            }

            // Coroutine for collecting next mock event for display
            launch {
                MockTravelInformation.getNextEventInformation().collect { next: TravelInformation ->
                    Log.d(TAG, "Collecting: $next")
                    _uiState.update { currentState -> currentState.copy(travelInformation = next) }
                }
            }

        }
    }
}

// TODO:
//  Behöver vi ha lon & lat i ui (förutom för de gps funktionerna som ligger i HomeScreen) eller
//  är det för att trigga uppdatering av ui?
data class UiState(
    val currentLatitude: String = "",
    val currentLongitude: String = "",
    val travelInformation: TravelInformation,
    val transitDeviationInformation: TransitDeviationInformation,
    val travelMode: TravelMode = TravelMode.Transit
)