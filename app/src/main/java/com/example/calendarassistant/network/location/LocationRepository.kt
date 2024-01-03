package com.example.calendarassistant.network.location

import android.location.Location
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LocationRepository {
    private val locationUpdates = MutableSharedFlow<Location>()

    suspend fun updateLocation(location: Location) {
        locationUpdates.emit(location)
    }

    fun getLocationUpdates(): SharedFlow<Location> = locationUpdates.asSharedFlow()
}
