package com.appedia.runtracker.services

import android.annotation.SuppressLint
import android.content.ContentProviderClient
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.appedia.runtracker.util.Constants.ACTION_PAUSE_SERVICE
import com.appedia.runtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.appedia.runtracker.util.Constants.ACTION_STOP_SERVICE
import com.appedia.runtracker.util.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import com.appedia.runtracker.util.Constants.LOCATION_UPDATE_INTERVAL
import com.appedia.runtracker.util.Constants.NOTIFICATION_ID
import com.appedia.runtracker.util.NotificationUtils
import com.appedia.runtracker.util.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng

typealias Path = MutableList<LatLng>
typealias ListOfPaths = MutableList<Path>

class TrackingService : LifecycleService() {

    private val TAG = TrackingService::class.java.simpleName
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        var serviceState = MutableLiveData<ServiceState>()
        val runPaths = MutableLiveData<ListOfPaths>()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called")
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        serviceState.observe(this, { serviceState ->
            Log.d(TAG, "Service state observer called. Value changed to $serviceState")
            if (serviceState == ServiceState.RUNNING) {
                Log.d(TAG, "Start location tracking")
                startTrackingServiceInForeground()
            } else if (serviceState == ServiceState.PAUSED) {
                Log.d(TAG, "Pause location tracking")
            } else if (serviceState == ServiceState.STOPPED) {
                Log.d(TAG, "Stop location tracking")
            }
        })
    }

    private fun postInitialValues() {
        runPaths.postValue(mutableListOf())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            Log.d(TAG, "Received action = ${it.action}")
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Log.d(TAG, "ServiceState is currently = ${serviceState.value}")
                    serviceState.postValue(ServiceState.RUNNING)
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d(TAG, "Service was running. Now being PAUSED")
                    serviceState.postValue(ServiceState.PAUSED)
                    stopLocationUpdates()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "Service now being STOPPED")
                    serviceState.postValue(ServiceState.STOPPED)
                    stopTrackingService()
                }
                else -> Log.d(TAG, "Unknown action. Bye bye")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopTrackingService() {
        stopLocationUpdates()
        // DO MORE STUFF HERE
        Log.d(TAG,"# of Paths in Run = ${runPaths.value?.size} \nAll paths = \n${runPaths.value}")
    }

    private fun addEmptyPath() {
        Log.d(TAG, "Added a new empty path in list of paths")
        runPaths.value?.apply {
            add(mutableListOf())
            runPaths.postValue(this)
        } ?: runPaths.postValue(mutableListOf(mutableListOf()))
    }

    private fun addLocationInPath(location: Location?) {
        location?.let {
            val point = LatLng(location.latitude, location.longitude)
            runPaths.value?.apply {
                // Take the last Path in the ListOfPaths(runPaths) and add this new location
                last().add(point)
                runPaths.postValue(this)
            }
        }
    }

    private fun startTrackingServiceInForeground() {
        addEmptyPath()
        startForeground(NOTIFICATION_ID, NotificationUtils.getRunTrackingNotification(this))
        requestLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (PermissionUtils.hasLocationPermission(this)) {
            val locationRequest = LocationRequest.create().apply {
                interval = LOCATION_UPDATE_INTERVAL
                fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                priority = PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.let { locations ->
                for (location in locations) {
                    if (serviceState.value == ServiceState.RUNNING) {
                        addLocationInPath(location)
                        Log.d(TAG, "Fetched new location = ${location.latitude},${location.longitude}")
                    }
                }
            }
        }
    }

}