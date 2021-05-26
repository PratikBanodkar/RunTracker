package com.appedia.runtracker.util

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RunTimer {

    private val TAG = "RunTimer"
    var runDuration = MutableLiveData<String>()
    var runDurationNotification = MutableLiveData<String>()
    private var startTimeStamp: Long = 0
    private var stopTime: Long = 0
    private var running = false
    private var totalRunTime = 0L
    private var lapTime = 0L
    private var lapStartTimeStamp = 0L
    private var nextSecondToLookFor = 1

    init {
        Log.d(TAG, "---------------TIMER INIT---------------")
        this.startTimeStamp = System.currentTimeMillis()
        this.lapStartTimeStamp = System.currentTimeMillis()
    }

    fun start() {
        Log.d(TAG, "---------------TIMER START---------------")
        running = true
        CoroutineScope(Dispatchers.Main).launch {
            while (running) {
                lapTime = System.currentTimeMillis() - lapStartTimeStamp
                Log.d(TAG, "TIMER POSTING NEW VALUE")
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

    private fun nextSecondHasElapsed(ms: Long): Boolean {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms)
        return seconds >= nextSecondToLookFor
    }

    fun pause() {
        Log.d(TAG, "---------------TIMER PAUSE---------------")
        running = false
    }

    fun resume() {
        Log.d(TAG, "---------------TIMER RESUME---------------")
        running = true
        lapStartTimeStamp = System.currentTimeMillis()
        start()
    }

    fun stop() {
        Log.d(TAG, "---------------TIMER STOP---------------")
        stopTime = System.currentTimeMillis()
        running = false
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
