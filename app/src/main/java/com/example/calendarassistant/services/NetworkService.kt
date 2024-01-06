package com.example.calendarassistant.services

import android.util.Log
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.model.mock.travel.MockTravelInformation
import com.example.calendarassistant.network.GoogleApi
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.network.SlApi
import com.example.calendarassistant.network.dto.google.directions.GoogleDirectionsResponse
import com.example.calendarassistant.network.dto.google.directions.internal.DepartureTime
import com.example.calendarassistant.network.dto.google.directions.internal.EndLocation
import com.example.calendarassistant.network.dto.google.directions.internal.Legs
import com.example.calendarassistant.network.dto.google.directions.internal.Steps
import com.example.calendarassistant.network.dto.sl.realtimeData.internal.ResponseData
import com.example.calendarassistant.network.location.LocationRepository
import com.example.calendarassistant.utilities.DateHelpers
import java.time.ZonedDateTime
import java.util.Timer
import kotlin.concurrent.schedule

private const val TAG = "NetworkService"

class NetworkService : INetworkService {



/*    override suspend fun getTimeToLeave(travelMode: TravelMode) {
        try {
            val lastLocationCoordinates = LocationRepository.getLastLocation()
                ?: throw INetworkService.NetworkException("Location not found")
            // Gets the next event happening from the mock
            val nextEvent = MockEvent.getMockEvents().first()
            // Sets arrival time for google directions to the event start-time
            val arrivalTime = ZonedDateTime.parse(nextEvent.start).toEpochSecond()
            // Google api-call
            val response = fetchGoogleDirections(
                arrivalTime,
                lastLocationCoordinates,
                nextEvent.location,
                travelMode
            )
            val legs = response.routes.first().legs.first()
            // Collects steps where transit
            val transitSteps = extractTransitSteps(legs.steps)
            // Contains time of departure in text and unix time
            val departureInformation = legs.departureTime

            if (departureInformation != null) {
                val departureTimeHHMM = calculateDepartureTimeHHMM(departureInformation)

                updateMockTravelInformation(
                    departureTimeHHMM,
                    legs.departureTime?.text,
                    legs.endLocation,
                    transitSteps
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
        }
    }

    private suspend fun fetchGoogleDirections(
        arrivalTime: Long,
        origin: Pair<String, String>,
        destination: String,
        mode: TravelMode
    ): GoogleDirectionsResponse {
        val response = GoogleApi.getDirectionsByCoordinatesOriginDestinationPlace(
            arrivalTime = arrivalTime,
            origin = origin,
            destination = destination,
            mode = mode
        )
        if (!response.isSuccessful) {
            throw INetworkService.NetworkException("Unsuccessful network call to Google")
        }
        return response.body()!!
    }

    *//**
     * Filters and returns a list of steps that involve transit.
     *//*
    private fun extractTransitSteps(steps: List<Steps>): List<Steps> =
        steps.filter { it.travelMode == "TRANSIT" }

    private fun calculateDepartureTimeHHMM(departureTime: DepartureTime?): String {
        // Calculate the time to leave (eg: "1h15m")
        val currentTime = ZonedDateTime.now().toEpochSecond()
        return departureTime?.value?.let {
            DateHelpers.formatSecondsToHourMinutes(it - currentTime)
        } ?: ""
    }

    private fun updateMockTravelInformation(departureTimeHHMM: String, departureTimeText: String?, endLocation: EndLocation?, transitSteps: List<Steps>) {
        // Updates set new info, which updates the ui
        MockTravelInformation.setTravelInformation(
            departureTimeHHMM,
            departureTime,
            Pair(endLocation?.lat, endLocation?.lng),
            transitSteps = transitSteps
        )
    }*/

    /**
     *  Makes api call to Google directions and updates the next event info
     */
    override suspend fun getTimeToLeave(travelMode: TravelMode) {
    /**
     *  Makes api call to Google directions and updates the next event info
     */
    override suspend fun getTravelInformation(travelMode: TravelMode) {
        try {
            val lastLocationCoordinates = LocationRepository.getLastLocation() ?: throw INetworkService.NetworkException("Location not found")
            // Gets the next event happening from the mock
            val nextEvent = MockEvent.getMockEvents().first()
            // Sets arrival time for google directions to the event start-time
            val arrivalTime = ZonedDateTime.parse(nextEvent.start).toEpochSecond()
            // Google api-call
            val response = GoogleApi.getDirectionsByCoordinatesOriginDestinationPlace(
                arrivalTime = arrivalTime,
                origin = lastLocationCoordinates,
                destination = nextEvent.location,
                mode = travelMode
            )
            if (!response.isSuccessful) {
                throw INetworkService.NetworkException("Unsuccessful network call to Google Maps api")
            }

            val legs = response.body()!!.routes.first().legs.first()

            Log.d(TAG, legs.toString())

            val steps = legs.steps

            // Collects steps where transit
            // TODO: use this list to make api calls for traffic events (delays)
            val transitSteps: MutableList<Steps> = mutableListOf()
            for (element in steps) {
                if (element.travelMode == "TRANSIT") {
                    transitSteps.add(element)
                }
            }

            // Contains time of departure in text and unix time
            val departureInformation = legs.departureTime
            // Contains coordinates for end location (used for opening google maps from ui)
            val endLocation = legs.endLocation

            if (departureInformation != null) {
                val departureTime = departureInformation.text

                // Calculate the time to leave (eg: "1h15m")
                val currentTime = ZonedDateTime.now().toEpochSecond()
                val departureTimeHHMM = DateHelpers.formatSecondsToHourMinutes(
                    departureInformation.value?.minus(
                        currentTime
                    )
                )
                // Updates set new info, which updates the ui
                MockTravelInformation.setTravelInformation(
                    departureTimeHHMM, departureTime, Pair(endLocation?.lat, endLocation?.lng), transitSteps = transitSteps
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
        }
    }


/*
    override suspend fun getTravelInformation(travelMode: TravelMode): Any? { //TODO: change return value
        return try {
            when (travelMode) {
                TravelMode.Transit -> getTransitTravelInformation()
                TravelMode.Driving -> getDrivingTravelInformation()
                TravelMode.Bicycling -> getBicyclingTravelInformation()
                TravelMode.Walking -> getWalkingTravelInformation()
                else -> null
            }
        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
        }
    }
*/

/*
    private suspend fun getTransitTravelInformation(): Any {
        TODO("Not yet implemented")

        return try {
            // todo: hämta info från google anrop
            // DepartureStop.name, ArrivalStop.name (eller position() -> lon lat, men då måste anrop ändras)
            // för alla transporter (ej gång emellan dem, alltså inte ""Steps" -> "travel_mode": "WALKING"")
            val responseSiteId = SlApi.getSiteIdByStationName("T-centralen")

            // DepartureTime.value, ArrivalTime.value
            // för alla avgångar och ankomster
            val responseRealtime = SlApi.getRealtimeDataBySiteId("9001")

        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
        }
    }
*/

/*
    private fun getDrivingTravelInformation(): Any? {
        TODO("Not yet implemented")
    }

    private fun getBicyclingTravelInformation(): Any? {
        TODO("Not yet implemented")
    }

    private fun getWalkingTravelInformation(): Any? {
        TODO("Not yet implemented")
    }
*/


    fun hello(): String {
        return "Hello"
    }
}

interface INetworkService {
    class NetworkException(message: String) : Exception()

    suspend fun getTravelInformation(travelMode: TravelMode)

    //suspend fun getTravelInformation(travelMode: TravelMode): Any?
}