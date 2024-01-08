package com.example.calendarassistant.network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Retrofit {

    private const val BASE_URL_TRAFIKVERKET = "https://api.trafikinfo.trafikverket.se"
    private const val BASE_URL_GOOGLE_MAPS = "https://maps.googleapis.com";
    private const val BASE_URL_Sl = "https://api.sl.se"

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

    val slInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_Sl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}