package com.example.calendarassistant.network.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import com.example.calendarassistant.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationClient(
    private val context: Context, private val client: FusedLocationProviderClient
) : ILocationClient {
    @SuppressLint("MissingPermission")
    override fun getLocationsUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if (!context.hasLocationPermission()) {
                throw ILocationClient.LocationException("Missing permissions")
            }
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                throw ILocationClient.LocationException("GPS is disabled")
            }

            val request = LocationRequest.Builder(interval).build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }
            }

            client.requestLocationUpdates(
                request, locationCallback, Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location {
        if (!context.hasLocationPermission()) {
            throw ILocationClient.LocationException("Missing permissions")
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            throw ILocationClient.LocationException("GPS is disabled")
        }

        return suspendCoroutine { continuation ->
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    continuation.resume(location)
                    locationManager.removeUpdates(this)
                }

                override fun onProviderEnabled(provider: String) {
                    // Implement as needed
                }

                override fun onProviderDisabled(provider: String) {
                    // Implement as needed
                }
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
        }
    }

}