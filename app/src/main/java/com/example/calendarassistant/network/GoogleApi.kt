package com.example.calendarassistant.network

import com.example.calendarassistant.enums.TravelMode
import com.example.calendarassistant.network.dto.google.directions.GoogleDirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

private const val mapsApiKey = "AIzaSyCdZXOjkf2AWnwZoGQy5rzoJ_lToiK8DpE" // Bör lagras som en env.

private interface IDirectionsApi {
    // Api calls for the directions Api
    @GET("/maps/api/directions/json")
    suspend fun getDirectionsByPlace(
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
    // Only for testing
    suspend fun getDirectionsByPlace(
        origin: String,
        destination: String,
        mode: TravelMode
    ): Response<GoogleDirectionsResponse> {
        return directionsInstance.getDirectionsByPlace(
            origin, destination, mode.toString()
        )
    }
}