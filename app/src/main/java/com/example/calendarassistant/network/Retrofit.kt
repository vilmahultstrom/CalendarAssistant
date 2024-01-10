package com.example.calendarassistant.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Retrofit {

    private const val BASE_URL_TRAFIKVERKET = "https://api.trafikinfo.trafikverket.se"
    private const val BASE_URL_GOOGLE_MAPS = "https://maps.googleapis.com"
    private const val BASE_URL_SL_REALTIME = "https://api.sl.se/"
    private const val BASE_URL_SL_NEARBY_STOPS = "https://journeyplanner.integration.sl.se/"
    private const val BASE_URL_SL_LOOK_UP = "https://journeyplanner.integration.sl.se/"


    val trafikverketInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_TRAFIKVERKET)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val googleDirectionsInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_GOOGLE_MAPS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val slRealtimeInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SL_REALTIME)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val slNearbyStopsInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SL_NEARBY_STOPS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val slLookUpInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SL_LOOK_UP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}