package com.example.calendarassistant.network

import android.util.Log
import com.example.calendarassistant.network.dto.sl.stationLookup.SlStationLookupResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val SL_API_STATION_LOOKUP_KEY = "5c28af3beab949be9f157415e92ef5b5" // todo: lagras som en env.

private interface ISlLookUpApi {

    // Api call for the 'SL Stop lookup v1.0' api
    @GET("v1/typeahead.json")
    suspend fun getSiteId(
        @Query("searchString") searchString: String,
        @Query("stationsOnly") stationsOnly: Boolean,
        @Query("key") key: String = SL_API_STATION_LOOKUP_KEY
    ): Response<SlStationLookupResponse>

}

object SlLookUpApi {

    private val slLookUpInstance =
        Retrofit.slLookUpInstance.create(ISlLookUpApi::class.java)

    suspend fun getSiteIdByStationName(
        stationName: String
    ): Response<SlStationLookupResponse> {
        return slLookUpInstance.getSiteId(stationName, true)
    }
}