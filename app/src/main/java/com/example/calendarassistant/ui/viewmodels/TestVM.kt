package com.example.calendarassistant.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.model.mock.calendar.MockCalendarEvent
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.model.mock.calendar.NextEventInformation
import com.example.calendarassistant.network.GoogleApi
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

    private val _mockEvents = MutableStateFlow(MockEvent.getMockEvents())
    val mockEvents: StateFlow<List<MockCalendarEvent>> = _mockEvents

    private val _uiState = MutableStateFlow(UiState(
        events = _mockEvents.value,
        nextEventInformation = NextEventInformation()
    ))
    val uiState: StateFlow<UiState> = _uiState

    private var isFetchingLocationData: Boolean = false

    private val _startServiceAction = mutableStateOf<Event<String>?>(null)
    val startServiceAction: State<Event<String>?> = _startServiceAction

    // Start fetching gps data
    fun onStartServiceClicked() {
        if (!isFetchingLocationData) {
            _startServiceAction.value = Event(LocationService.ACTION_START)
            viewModelScope.launch {
                networkService.getTimeToLeave(TravelMode.Transit)
            }
            isFetchingLocationData = true
        } else {
            _startServiceAction.value = Event(LocationService.ACTION_STOP)
            isFetchingLocationData = false
            _uiState.update { _uiState.value.copy(currentLatitude = "", currentLongitude = "") }
        }
    }

    fun login() {
        viewModelScope.launch {
            Log.d(TAG, "loggin in button pressed")
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
            launch {
                _startServiceAction.value =
                    Event(LocationService.ACTION_GET) // sets current location so its not null
                delay(10000) // delay for gps init
                networkService.getTimeToLeave(_uiState.value.travelMode)
            }

            launch {
                LocationRepository.getLocationUpdates().collect { location ->
                    _uiState.update {
                        _uiState.value.copy(
                            currentLatitude = location.latitude.toString(),
                            currentLongitude = location.longitude.toString()
                        )
                    }
                    networkService.getTimeToLeave(_uiState.value.travelMode)
                }
            }

            launch {
                MockEvent.getNextEventInformation().collect { next: NextEventInformation ->
                    Log.d(TAG, "Collecting: $next")
                    _uiState.update { currentState -> currentState.copy(nextEventInformation = next) }
                }
            }

        }
    }
}

data class UiState(
    val currentLatitude: String = "",
    val currentLongitude: String = "",
    val events: List<MockCalendarEvent> = listOf(),
    val nextEventInformation: NextEventInformation,
    val travelMode: TravelMode = TravelMode.Transit
)