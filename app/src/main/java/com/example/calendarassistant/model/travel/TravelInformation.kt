package com.example.calendarassistant.model.travel

import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.utilities.TimeToLeaveDisplay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 *  Keeps and handles travel information from Google Maps
 */
object TravelInformation {

    private val _transitSteps = MutableStateFlow<List<Steps>>(listOf())
    var transitSteps: StateFlow<List<Steps>> = _transitSteps

    private var travelInformationData = MutableSharedFlow<TravelInformationData>()

    suspend fun setTravelInformation(
        departureTimeHHMM: TimeToLeaveDisplay,
        departureTime: String?,
        endLocation: Pair<Double?, Double?>,
        transitSteps: List<Steps>,
    ) {
        travelInformationData.emit(
            TravelInformationData(
                departureTime = departureTime,
                departureTimeHHMM = departureTimeHHMM,
                destinationCoordinates = endLocation
            )
        )

        _transitSteps.value = transitSteps
    }

    fun getNextEventTravelInformation() : SharedFlow<TravelInformationData> = travelInformationData.asSharedFlow()
}

data class TravelInformationData (
    var departureTimeHHMM: TimeToLeaveDisplay = TimeToLeaveDisplay(),
    var departureTime: String? = "",
    var destinationCoordinates: Pair<Double?, Double?> = Pair(null, null),
)

