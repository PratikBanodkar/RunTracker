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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NewTrackingService : LifecycleService() {

    private val TAG = NewTrackingService::class.java.simpleName

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /*@Inject
    lateinit var runTimer: RunTimer*/

    private var firstRun = true
    private var startTimeStamp: Long = 0
    private var stopTime: Long = 0
    private var running = false
    private var totalRunTime = 0L
    private var lapTime = 0L
    private var lapStartTimeStamp = 0L
    private var nextSecondToLookFor = 1


    companion object {
        val runPaths = MutableLiveData<ListOfPaths>()

        //val runTimeForUi = MutableLiveData<String>()
        var serviceState = MutableLiveData<ServiceState>()
        var runDuration = MutableLiveData<String>()
        var runDurationNotification = MutableLiveData<String>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        observeServiceState()
        observeTimerData()
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
        serviceState.observe(this, { state ->
            Log.d(TAG, "Service state got observed with state = $state")
            when (state) {
                ServiceState.RUNNING -> {
                    if (firstRun) {
                        Log.d(TAG, "Start location tracking")
                        startTrackingServiceInForeground()
                        startTimer()
                        //runTimer.start()
                        firstRun = false
                    } else {
                        Log.d(TAG, "Resume location tracking")
                        resumeTrackingService()
                        resumeTimer()
                        //runTimer.resume()
                    }
                }
                ServiceState.PAUSED -> {
                    Log.d(TAG, "Pause location tracking")
                    pauseTrackingService()
                    pauseTimer()
                    //runTimer.pause()
                }
                ServiceState.STOPPED -> {
                    Log.d(TAG, "Stop location tracking")
                    stopTrackingService()
                    stopTimer()
                    //runTimer.stop()
                }
            }
        })
    }

    private fun startTrackingServiceInForeground() {
        addEmptyPath()
        startForeground(NOTIFICATION_ID, NotificationUtils.getRunTrackingNotification(this))
        requestLocationUpdates()
    }

    private fun startTimer() {
        Log.d(TAG, "---------------TIMER START---------------")
        running = true
        CoroutineScope(Dispatchers.Main).launch {
            while (running) {
                lapTime = System.currentTimeMillis() - lapStartTimeStamp
                //Log.d(TAG,"---------------TIMER POSTING NEW VALUE---------------")
                runDuration.postValue(getDisplayTime(totalRunTime + lapTime))
                if (nextSecondHasElapsed(totalRunTime + lapTime)) {
                    runDurationNotification.postValue(getNotificationDisplayTime(totalRunTime + lapTime))
                    nextSecondToLookFor++
                }

                delay(50L)
            }
            totalRunTime += lapTime
        }
    }

    private fun resumeTrackingService() {
        addEmptyPath()
        requestLocationUpdates()
    }

    private fun resumeTimer() {
        Log.d(TAG, "---------------TIMER RESUME---------------")
        running = true
        lapStartTimeStamp = System.currentTimeMillis()
        startTimer()
    }

    private fun pauseTrackingService() {
        stopLocationUpdates()
        runDurationNotification.value?.let {
            NotificationUtils.updateNotification(this, it, ServiceState.PAUSED)
        }
    }

    private fun pauseTimer() {
        Log.d(TAG, "---------------TIMER PAUSE---------------")
        running = false
    }

    private fun stopTrackingService() {
        stopLocationUpdates()
        // DO MORE STUFF HERE
        Log.d(TAG, "# of Paths in Run = ${runPaths.value?.size} \nAll paths = \n${runPaths.value}")
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    private fun stopTimer() {
        Log.d(TAG, "---------------TIMER STOP---------------")
        stopTime = System.currentTimeMillis()
        running = false
        totalRunTime = 0L
        lapTime = 0L
    }

    private fun postInitialValues() {
        runPaths.postValue(mutableListOf())
        this.startTimeStamp = System.currentTimeMillis()
        this.lapStartTimeStamp = System.currentTimeMillis()
    }

    private fun observeTimerData() {
        runDurationNotification.observe(this, { notificationTimeString ->
            serviceState.value?.let {
                if (serviceState.value != ServiceState.STOPPED) {
                    NotificationUtils.updateNotification(
                        this, notificationTimeString,
                        it
                    )
                }
            }
        })
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
                        Log.d(
                            TAG,
                            "Fetched new location = ${location.latitude},${location.longitude}"
                        )
                    }
                }
            }
        }
    }

    private fun nextSecondHasElapsed(ms: Long): Boolean {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms)
        return seconds >= nextSecondToLookFor
    }

    private fun getDisplayTime(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                "${if (milliseconds < 10) "0" else ""}$milliseconds"
    }

    private fun getNotificationDisplayTime(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

}