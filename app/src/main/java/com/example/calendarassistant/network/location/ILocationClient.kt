package com.example.calendarassistant.network.location

import android.location.Location
import kotlinx.coroutines.flow.Flow


interface ILocationClient {
    fun getLocationsUpdates(interval: Long): Flow<Location>

    suspend fun getCurrentLocation(): Location?

    class LocationException(message: String) : Exception()

}