package com.example.calendarassistant.network

import com.example.calendarassistant.network.dto.sl.realtimeData.SlRealtimeDataResponse
import com.example.calendarassistant.network.dto.sl.stationLookup.SlStationLookupResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val TAG = "SlApi"
private const val SL_API_REALTIME_KEY = "02e6c4848dae4e39ac256633f954cecc" // todo: lagras som en env.
private const val SL_API_STATION_LOOKUP_KEY = "5c28af3beab949be9f157415e92ef5b5" // todo: lagras som en env.

private interface ISlApi {

    // Api call for the 'SL Departures v4.0' api
    @GET("/api2/realtimedeparturesV4.json")
    suspend fun getRealtimeData(
        @Query("siteId") siteId: String,
        @Query("timeWindow") timeWindow: String,
        @Query("key") key: String = SL_API_REALTIME_KEY
    ): Response<SlRealtimeDataResponse>

    // Api call for the 'SL Stop lookup v1.0' api
    @GET("/v1/typeahead.json")
    suspend fun getSiteId(
        @Query("searchString") searchString: String,
        @Query("stationsOnly") stationsOnly: Boolean,
        @Query("key") key: String = SL_API_STATION_LOOKUP_KEY
    ): Response<SlStationLookupResponse>
}

object SlApi {

    private val slInstance =
        Retrofit.slInstance.create(ISlApi::class.java)

    suspend fun getSiteIdByStationName(
        stationName: String
    ): Response<SlStationLookupResponse> {
//        return try {
            return slInstance.getSiteId(stationName, true)
//        } catch (e: Exception) {
//            Log.e(TAG, e)
//        }
    }

    suspend fun getRealtimeDataBySiteId(
        siteId: String
    ): Response<SlRealtimeDataResponse> {
        val timeWindow = "60" // in minutes //TODO: in api max time is 60 min, default is 30 min - move value and be possible to change?
        return slInstance.getRealtimeData(siteId, timeWindow)
    }
}
