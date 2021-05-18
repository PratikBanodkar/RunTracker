package com.appedia.runtracker.util

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

class RunTimer {


    companion object{
        var runDuration = MutableLiveData<String>()
    }

    private val TAG = RunTimer::class.java.simpleName
    private var startTimeStamp: Long = 0
    private var stopTime: Long = 0
    private var running = false
    private var totalRunTime = 0L
    private var lapTime = 0L
    private var lapStartTimeStamp = 0L
    init {
        this.startTimeStamp = System.currentTimeMillis()
        this.lapStartTimeStamp = System.currentTimeMillis()
    }

    fun start() {
        running = true
        CoroutineScope(Dispatchers.Main).launch {
            while (running) {
                lapTime = System.currentTimeMillis() - lapStartTimeStamp
                // TODO postValue(totalTime + lapTime)
                runDuration.postValue(getDisplayTime(totalRunTime + lapTime))
                delay(50L)
            }
            totalRunTime += lapTime
        }
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

}
