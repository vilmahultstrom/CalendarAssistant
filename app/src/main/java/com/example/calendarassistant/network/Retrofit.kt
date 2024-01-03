package com.example.calendarassistant.network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object Retrofit {

    private const val trafikverketBaseUrl = "https://api.trafikinfo.trafikverket.se"
    private const val googleMapsBaseUrl = "https://maps.googleapis.com";

    val trafikverketInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(trafikverketBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val googleDirectionsInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(googleMapsBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



}