package com.example.calendarassistant.network

import com.example.calendarassistant.network.dto.sl.nearbyStops.SlNearbyStopsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val SL_API_NEARBY_STOPS_KEY = "879e5122273e41009f8b72cf83099803" // todo: lagras som en env.

private interface ISlNearbyStopsApi {

    // Api call for the 'SL Nearby Stops v2.0' api
    @GET("v1/nearbystopsv2.json")
    suspend fun getSiteId(
        @Query("originCoordLat") originCoordLat: String,
        @Query("originCoordLong") originCoordLong: String,
        @Query("type") type: String,
        @Query("maxNo") maxNo: Int,
        @Query("key") key: String = SL_API_NEARBY_STOPS_KEY
    ): Response<SlNearbyStopsResponse>

}

object SlNearbyStopsApi {

    private val slNearbyStopsInstance =
        Retrofit.slNearbyStopsInstance.create(ISlNearbyStopsApi::class.java)

    suspend fun getSiteIdByCoordinates(
        originLat: String,
        originLng: String
    ): Response<SlNearbyStopsResponse> {
        val type = "S" // 'Stations only'
        val maxType = 3 // Number of result
        return slNearbyStopsInstance.getSiteId(originLat, originLng, "S", 3)
    }
}