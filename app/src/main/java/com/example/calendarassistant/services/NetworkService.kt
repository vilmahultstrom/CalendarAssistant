package com.example.calendarassistant.services

import android.util.Log
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.network.GoogleApi
import com.example.calendarassistant.network.SlApi
import com.example.calendarassistant.network.location.LocationRepository
import com.example.calendarassistant.utilities.DateHelpers
import java.time.ZonedDateTime

private const val TAG = "NetworkService"

class NetworkService : INetworkService {


    override suspend fun getTimeToLeave(travelMode: TravelMode) {
        try {
            val lastLocationCoordinates = LocationRepository.getLastLocation() ?: return
            val nextEvent = MockEvent.getMockEvents().first()
            val arrivalTime = ZonedDateTime.parse(nextEvent.start).toEpochSecond()
            val response = GoogleApi.getDirectionsByCoordinatesOriginDestinationPlace(
                arrivalTime = arrivalTime,
                origin = lastLocationCoordinates,
                destination = nextEvent.location,
                mode = travelMode
            )
            if (!response.isSuccessful) {
                return
            }

            val legs = response.body()!!.routes.first().legs.first()
            val departureInformation = legs.departureTime
            val endLocation = legs.endLocation

            if (departureInformation != null) {
                val currentTime = ZonedDateTime.now().toEpochSecond()
                val departureTime = departureInformation.text
                val departureTimeHHMM = DateHelpers.formatSecondsToHourMinutes(
                    departureInformation.value?.minus(
                        currentTime
                    )
                )
                // For ui update
                MockEvent.setNextEventInformation(
                    departureTimeHHMM, departureTime, Pair(endLocation?.lat, endLocation?.lng)
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
        }
    }


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

    private fun getDrivingTravelInformation(): Any? {
        TODO("Not yet implemented")
    }

    private fun getBicyclingTravelInformation(): Any? {
        TODO("Not yet implemented")
    }

    private fun getWalkingTravelInformation(): Any? {
        TODO("Not yet implemented")
    }


    fun hello(): String {
        return "Hello"
    }


}

interface INetworkService {
    class NetworkException(message: String) : Exception()

    suspend fun getTimeToLeave(travelMode: TravelMode)

    suspend fun getTravelInformation(travelMode: TravelMode): Any?
}