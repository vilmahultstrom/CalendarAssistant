package com.example.calendarassistant.network.location

import android.location.Location
import kotlinx.coroutines.flow.Flow


interface LocationClient {
    fun getLocationsUpdates(interval: Long): Flow<Location>

    class LocationException(message: String) : Exception()

}