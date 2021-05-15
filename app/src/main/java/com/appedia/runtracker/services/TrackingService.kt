package com.appedia.runtracker.services

import android.annotation.SuppressLint
import android.content.ContentProviderClient
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng

typealias SinglePolyLine = MutableList<LatLng>
typealias ManyPolylines = MutableList<SinglePolyLine>


class TrackingService : LifecycleService() {

    private val TAG = TrackingService::class.java.simpleName
    var isServiceRunning = false
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val polyLines = MutableLiveData<ManyPolylines>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this,{
            updateLocationTracking(it)
        })
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        polyLines.postValue(mutableListOf())
    }

    private fun addEmptySinglePolyline() {
        polyLines.value?.apply {
            add(mutableListOf())
            polyLines.postValue(this)
        } ?: polyLines.postValue(mutableListOf(mutableListOf()))
    }

    private fun addLocationPoint(location: Location?) {
        location?.let {
            val point = LatLng(location.latitude, location.longitude)
            polyLines.value?.apply {
                last().add(point)
                polyLines.postValue(this)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (!isServiceRunning) {
                        startServiceInForeground()
                        isServiceRunning = true
                    } else {
                        Log.d(TAG, "Need to resume service")
                    }
                }

                ACTION_PAUSE_SERVICE ->
                    Log.d(TAG, "Paused service")
                ACTION_STOP_SERVICE ->
                    Log.d(TAG, "Stopped service")
                else ->
                    Log.d(TAG, "Unknown command sent to service")
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startServiceInForeground() {
        addEmptySinglePolyline()
        isTracking.postValue(true)
        startForeground(NOTIFICATION_ID, NotificationUtils.getRunTrackingNotification(this))
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addLocationPoint(location)
                        Log.d(TAG,"Fetched new location = ${location.latitude},${location.longitude}")
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
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
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
}