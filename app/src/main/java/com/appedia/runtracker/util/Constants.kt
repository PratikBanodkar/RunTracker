package com.appedia.runtracker.util

import android.graphics.Color
import android.location.Location
import com.appedia.runtracker.services.Path
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Constants {

    const val RUN_TRACKER_DATABASE_NAME = "run_tracker_db"
    const val REQUEST_CODE_LOCATION_PERMISSION = 0
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_UPDATE_INTERVAL = 2000L
    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 16f
    const val MET = 4

    fun calculatePathDistance(path: Path): Float {
        var distance = 0f
        for (i in 0..path.size - 2) {
            val point1 = path[i]
            val point2 = path[i + 1]
            val results = FloatArray(1)
            Location.distanceBetween(
                point1.latitude,
                point1.longitude,
                point2.latitude,
                point2.longitude,
                results
            )
            distance += results[0]
        }
        return distance
    }

    fun getDistanceInKilometers(distanceInMeters: Float) = distanceInMeters / 1000f

    fun getRunTimeInHours(runTimeForUi: String?): Float {
        val millis = runTimeForUi?.split(':')?.get(3)?.toLong() ?: 0L
        val seconds = runTimeForUi?.split(':')?.get(2)?.toLong() ?: 0L
        val minutes = runTimeForUi?.split(':')?.get(1)?.toLong() ?: 0L
        val hours = runTimeForUi?.split(':')?.get(0)?.toLong() ?: 0L
        var totalTimeInMillis = 0L
        totalTimeInMillis += millis
        totalTimeInMillis += (seconds * 1000)
        totalTimeInMillis += (minutes * 60 * 1000)
        totalTimeInMillis += (hours * 60 * 60 * 1000)
        return totalTimeInMillis / 1000f / 60 / 60
    }

    fun getRunDurationInMillis(runTimeForUi: String?): Long {
        val millis = runTimeForUi?.split(':')?.get(3)?.toLong() ?: 0L
        val seconds = runTimeForUi?.split(':')?.get(2)?.toLong() ?: 0L
        val minutes = runTimeForUi?.split(':')?.get(1)?.toLong() ?: 0L
        val hours = runTimeForUi?.split(':')?.get(0)?.toLong() ?: 0L
        var totalTimeInMillis = 0L
        totalTimeInMillis += millis
        totalTimeInMillis += (seconds * 1000)
        totalTimeInMillis += (minutes * 60 * 1000)
        totalTimeInMillis += (hours * 60 * 60 * 1000)
        return totalTimeInMillis
    }

    fun getFormattedDateForUI(runDateInMillis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = runDateInMillis
        }
        val dateFormat = SimpleDateFormat("dd MMMM yy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun getFormattedRunTimeForUI(runTimeInMillis: Long): String {
        var milliseconds = runTimeInMillis
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        val returnString = (if (hours > 0) "${hours}h" else "") +
                (if (minutes > 0) " ${minutes}m" else "") +
                (if (seconds > 0) " ${seconds}s" else "")
        return returnString.trim()
    }

    fun getFormattedDistanceForUI(distanceInMeters: Int) =
        String.format("%.1f", distanceInMeters / 1000f) + " km"

    fun getFormattedSpeedForUI(speedInKMPH: Float) = String.format("%.1f", speedInKMPH) + " km/h"

    fun getFormattedCaloriesForUI(caloriesBurned: Int) = "$caloriesBurned kcal"
}