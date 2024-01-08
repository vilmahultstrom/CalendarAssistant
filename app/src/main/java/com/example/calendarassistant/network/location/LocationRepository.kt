package com.example.calendarassistant.network.location

import android.location.Location
import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow


private const val TAG = "LocationRepository"
object LocationRepository {
    private val locationUpdates = MutableSharedFlow<Location>()
    private var currentLocation: Location? = null

    suspend fun updateLocation(location: Location) {
        currentLocation = location
        locationUpdates.emit(location)
    }

    fun getLastLocation() : Pair<String, String>? {
        val lastLatitude = currentLocation?.latitude
        val lastLongitude = currentLocation?.longitude

        if (lastLatitude == null || lastLongitude == null){
            return null
        }
        return Pair(lastLatitude.toString(), lastLongitude.toString())
    }

    fun setCurrentLocation(currentLocation: Location) {
        this.currentLocation = currentLocation
    }

    fun getLocationUpdates(): SharedFlow<Location> = locationUpdates.asSharedFlow()
}
