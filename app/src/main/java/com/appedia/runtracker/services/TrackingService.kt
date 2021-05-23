package com.appedia.runtracker.services

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.appedia.runtracker.util.Constants.ACTION_PAUSE_SERVICE
import com.appedia.runtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.appedia.runtracker.util.Constants.ACTION_STOP_SERVICE
import com.appedia.runtracker.util.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import com.appedia.runtracker.util.Constants.LOCATION_UPDATE_INTERVAL
import com.appedia.runtracker.util.Constants.NOTIFICATION_ID
import com.appedia.runtracker.util.NotificationUtils
import com.appedia.runtracker.util.PermissionUtils
import com.appedia.runtracker.util.RunTimer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

typealias Path = MutableList<LatLng>
typealias ListOfPaths = MutableList<Path>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private val TAG = TrackingService::class.java.simpleName

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @Inject
    lateinit var runTimer : RunTimer

    private var firstRun = true

    companion object {
        var serviceState = MutableLiveData<ServiceState>()
        val runPaths = MutableLiveData<ListOfPaths>()
        var runDuration = MutableLiveData<String>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        observeServiceState()
        observeTimer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            Log.d(TAG, "Received action = ${it.action}")
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Log.d(TAG, "Service is now RUNNING")
                    serviceState.postValue(ServiceState.RUNNING)
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d(TAG, "Service was running. Now being PAUSED")
                    serviceState.postValue(ServiceState.PAUSED)
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "Service now being STOPPED")
                    serviceState.postValue(ServiceState.STOPPED)
                }
                else -> Log.d(TAG, "Unknown action. Bye bye")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun observeServiceState() {
        serviceState.observe(this, { serviceState ->
            when (serviceState) {
                ServiceState.RUNNING -> {
                    if (firstRun) {
                        Log.d(TAG, "Start location tracking")
                        startTrackingServiceInForeground()
                        runTimer.start()
                        firstRun = false
                    } else {
                        Log.d(TAG, "Resume location tracking")
                        resumeTrackingService()
                        runTimer.resume()
                    }
                }
                ServiceState.PAUSED -> {
                    Log.d(TAG, "Pause location tracking")
                    pauseTrackingService()
                    runTimer.pause()
                }
                ServiceState.STOPPED -> {
                    Log.d(TAG, "Stop location tracking")
                    stopTrackingService()
                    runTimer.stop()
                }
            }
        })
    }

    private fun observeTimer() {
        RunTimer.runDurationNotification.observe(this, { notificationTimeString ->
            NotificationUtils.updateNotification(this, notificationTimeString)
        })
    }

    private fun postInitialValues() {
        runPaths.postValue(mutableListOf())
        runDuration.postValue("00:00:00:00")
    }

    private fun pauseTrackingService() {
        stopLocationUpdates()
    }

    private fun resumeTrackingService() {
        addEmptyPath()
        requestLocationUpdates()
    }

    private fun stopTrackingService() {
        stopLocationUpdates()
        // DO MORE STUFF HERE
        Log.d(TAG, "# of Paths in Run = ${runPaths.value?.size} \nAll paths = \n${runPaths.value}")
    }

    private fun addEmptyPath() {
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