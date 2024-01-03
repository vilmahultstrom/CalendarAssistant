package com.example.calendarassistant.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val mapsApiKey = "AIzaSyCdZXOjkf2AWnwZoGQy5rzoJ_lToiK8DpE"
private interface IDirectionsApi {
    // Api calls for the directions Api
    @GET("/maps/api/directions")
    suspend fun getDirectionsByPlace(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String = mapsApiKey
    ) : Response<String>


}
class GoogleApi {

    private val directionsInstance = Retrofit.googleDirectionsInstance.create(IDirectionsApi::class.java)

    /*
        Implement methods here eg:
    */
    // Only for testing
    suspend fun getDirectionsByPlace(origin: String, destination: String): Response<String> {
        return directionsInstance.getDirectionsByPlace("Boston, MA", "Concord, Ma");
    }
}