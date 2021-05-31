package com.appedia.runtracker.services

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Looper
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

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var runTimer: RunTimer

    private var firstRun = true

    companion object {
        var serviceState = MutableLiveData<ServiceState>()
        val runPaths = MutableLiveData<ListOfPaths>()
        val runTime = MutableLiveData<String>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        observeServiceState()
        observeTimer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    serviceState.postValue(ServiceState.RUNNING)
                }
                ACTION_PAUSE_SERVICE -> {
                    serviceState.postValue(ServiceState.PAUSED)
                }
                ACTION_STOP_SERVICE -> {
                    serviceState.postValue(ServiceState.STOPPED)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun observeServiceState() {
        serviceState.observe(this, { state ->
            when (state) {
                ServiceState.RUNNING -> {
                    if (firstRun) {
                        startTrackingServiceInForeground()
                        runTimer.start()
                        firstRun = false
                    } else {
                        resumeTrackingService()
                        runTimer.resume()
                    }
                }
                ServiceState.PAUSED -> {
                    pauseTrackingService()
                    runTimer.pause()
                }
                ServiceState.STOPPED -> {
                    firstRun = true
                    stopTrackingService()
                    serviceState.removeObservers(this)
                    runTimer.stop()
                }
                else -> { /* NO-OP */
                }
            }
        })
    }

    private fun observeTimer() {
        runTimer.runDurationNotification.observe(this, { notificationTimeString ->
            if (serviceState.value == ServiceState.RUNNING) {
                serviceState.value?.let {
                    NotificationUtils.updateNotification(this, notificationTimeString, it)
                }
            }
        })
        runTimer.runDuration.observe(this, { runDurationTimeString ->
            if (serviceState.value == ServiceState.RUNNING)
                runTime.postValue(runDurationTimeString)
        })
    }

    private fun postInitialValues() {
        runPaths.postValue(mutableListOf())
        runTime.postValue("00:00:00:00")
        serviceState.postValue(ServiceState.INIT)
    }

    private fun pauseTrackingService() {
        stopLocationUpdates()
        if (serviceState.value == ServiceState.RUNNING || serviceState.value == ServiceState.PAUSED) {
            runTimer.runDurationNotification.value?.let {
                NotificationUtils.updateNotification(this, it, ServiceState.PAUSED)
            }
        }
    }

    private fun resumeTrackingService() {
        addEmptyPath()
        requestLocationUpdates()
    }

    private fun stopTrackingService() {
        stopLocationUpdates()
        // DO MORE STUFF HERE
        postInitialValues()
        stopForeground(true)
        stopSelf()
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
                    }
                }
            }
        }
    }

}