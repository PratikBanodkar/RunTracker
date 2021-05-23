package com.appedia.runtracker.util

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RunTimer {


    companion object{
        var runDuration = MutableLiveData<String>()
        var runDurationNotification = MutableLiveData<String>()
    }

    private var startTimeStamp: Long = 0
    private var stopTime: Long = 0
    private var running = false
    private var totalRunTime = 0L
    private var lapTime = 0L
    private var lapStartTimeStamp = 0L
    private var nextSecondToLookFor = 1
    init {
        this.startTimeStamp = System.currentTimeMillis()
        this.lapStartTimeStamp = System.currentTimeMillis()
    }

    fun start() {
        running = true
        CoroutineScope(Dispatchers.Main).launch {
            while (running) {
                lapTime = System.currentTimeMillis() - lapStartTimeStamp
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
        running = false
    }

    fun resume() {
        running = true
        lapStartTimeStamp = System.currentTimeMillis()
        start()
    }

    fun stop() {
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
