package com.example.calendarassistant.network.location

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
private const val TAG = "LocationService"
class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: ILocationClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
            ACTION_GET -> {
                serviceScope.launch {
                    withContext(Dispatchers.Main) {
                        getCurrent()
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        locationClient.getLocationsUpdates(60000L).catch { e -> e.printStackTrace() }
            .onEach { location ->
                LocationRepository.updateLocation(location)
            }.launchIn(serviceScope)
    }

    private suspend fun getCurrent() {
        val location = locationClient.getCurrentLocation()
        LocationRepository.setCurrentLocation(location)
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_GET = "ACTION_GET"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}