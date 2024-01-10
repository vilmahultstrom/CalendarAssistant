package com.example.calendarassistant.network.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import com.example.calendarassistant.hasLocationPermission
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "LocationClient"

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

        val locationRequest =
            CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()
        val cancellationToken = CancellationTokenSource()

        return suspendCoroutine { continuation ->
            client.getCurrentLocation(locationRequest, cancellationToken.token)
                .addOnSuccessListener { location ->
                    // If location found success, resume with location
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    // else resume with exception
                    continuation.resumeWithException(exception)
                }
            // TODO: You may want to add an OnCanceledListener to handle cancellation.
        }

        //Log.d(TAG, location2.toString())

        /*

        val location = suspendCoroutine { continuation ->
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
            Log.d(TAG, "Getting location")
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)


         */
    }
    //Log.d(TAG, "Getting location 2")
    // return location2
}
