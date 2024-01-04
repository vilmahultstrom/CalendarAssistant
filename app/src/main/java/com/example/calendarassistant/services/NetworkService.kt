package com.example.calendarassistant.services

import android.util.Log
import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.model.mock.calendar.MockEvent
import com.example.calendarassistant.network.GoogleApi
import com.example.calendarassistant.network.location.LocationRepository
import com.example.calendarassistant.utilities.DateHelpers
import java.time.ZoneId
import java.time.ZoneOffset

private const val TAG = "NetworkService"
class NetworkService() : INetworkService {



    override suspend fun getTimeToLeave(travelMode: TravelMode) {
        try {

            val lastLocationCoordinates = LocationRepository.getLastLocation() ?: return


            Log.d(TAG, lastLocationCoordinates.toString())
            val nextEvent = MockEvent.getMockEvents().first()

            val arrivalTime =
                DateHelpers.convertToSystemTimeZone(nextEvent.start)?.minusMinutes(15)?.toEpochSecond()
                    ?: return   // TODO: Hantera bättre

            Log.d(TAG, arrivalTime.toString())

            Log.d(TAG, nextEvent.toString())

            val response = GoogleApi.getDirectionsByCoordinatesOriginDestinationPlace(
                arrivalTime = arrivalTime, origin = lastLocationCoordinates, destination = nextEvent.location, mode =travelMode
            )

            if (response.body() == null){
                return
            }

            Log.d(TAG, response.body().toString())

        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
        }
    }



    fun hello() : String {
        return "Hello"
    }




}

interface INetworkService {
    class NetworkException(message: String) : Exception()

    suspend fun getTimeToLeave(travelMode: TravelMode);

}