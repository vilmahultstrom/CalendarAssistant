package com.example.calendarassistant.network

import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.network.dto.google.directions.GoogleDirectionsResponse
import com.example.calendarassistant.network.dto.google.directions.internal.DepartureTime
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val mapsApiKey = "AIzaSyCdZXOjkf2AWnwZoGQy5rzoJ_lToiK8DpE" // TODO: Bör lagras som en env.

private interface IDirectionsApi {
    // Api calls for the directions Api
    @GET("/maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") key: String = mapsApiKey
    ): Response<GoogleDirectionsResponse>

    @GET("/maps/api/directions/json")
    suspend fun getDirectionsArrivalTime(
        @Query("arrival_time") arrivalTime: Long,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") key: String = mapsApiKey
    ): Response<GoogleDirectionsResponse>

    @GET("/maps/api/directions/json")
    suspend fun getDirectionsRouteInformation(
        @Query("arrival_time") arrivalTime: Long,
        @Query("departure_time") departureTime: Long,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") key: String = mapsApiKey
    ): Response<GoogleDirectionsResponse>

}

object GoogleApi {

    private val directionsInstance =
        Retrofit.googleDirectionsInstance.create(IDirectionsApi::class.java)

    /*
        Implement methods here eg:
    */
    suspend fun getDirectionsByPlace(
        origin: String,
        destination: String,
        mode: TravelMode
    ): Response<GoogleDirectionsResponse> {
        return directionsInstance.getDirections(
            origin, destination, mode.toString()
        )
    }

    // origin=41.43206,-81.38992
    suspend fun getDirectionsByCoordinates(
        origin: Pair<Float, Float>,
        destination: Pair<Float, Float>,
        mode: TravelMode
    ): Response<GoogleDirectionsResponse> {
        val originString = origin.first.toString() + "," + origin.second.toString()
        val destinationString = destination.first.toString() + "," + destination.second.toString()
        return directionsInstance.getDirections(originString, destinationString, mode.toString())
    }

    suspend fun getDirectionsByCoordinatesOriginDestinationPlace(
        arrivalTime: Long,
        origin: Pair<String, String>,
        destination: String,
        mode: TravelMode
    ): Response<GoogleDirectionsResponse> {
        val originString = origin.first + "," + origin.second
        return directionsInstance.getDirectionsArrivalTime(arrivalTime, originString, destination, mode.toString())
    }

}