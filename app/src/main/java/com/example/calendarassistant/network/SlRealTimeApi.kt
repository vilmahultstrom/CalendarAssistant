package com.example.calendarassistant.network

import com.example.calendarassistant.network.dto.sl.realtimeData.SlRealtimeDataResponse
import com.example.calendarassistant.network.dto.sl.stationLookup.SlStationLookupResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val TAG = "SlRealTimeApi"
private const val SL_API_REALTIME_KEY = "02e6c4848dae4e39ac256633f954cecc" // todo: lagras som en env.

private interface ISlRealTimeApi {

    // Api call for the 'SL Departures v4.0' api
    @GET("api2/realtimedeparturesV4.json")
    suspend fun getRealtimeData(
        @Query("siteId") siteId: String,
        @Query("timeWindow") timeWindow: String,
        @Query("key") key: String = SL_API_REALTIME_KEY
    ): Response<SlRealtimeDataResponse>

}

object SlRealTimeApi {

    private val slRealTimeInstance =
        Retrofit.slRealtimeInstance.create(ISlRealTimeApi::class.java)

    suspend fun getRealtimeDataBySiteId(
        siteId: String
    ): Response<SlRealtimeDataResponse> {
        val timeWindow = "60" // in minutes //TODO: in api max time is 60 min, default is 30 min - move value and be possible to change?
        return slRealTimeInstance.getRealtimeData(siteId, timeWindow)
    }
}
