package com.appedia.runtracker.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.appedia.runtracker.R
import com.appedia.runtracker.services.ServiceState
import com.appedia.runtracker.services.TrackingService
import com.appedia.runtracker.ui.MainActivity
import com.appedia.runtracker.util.Constants.NOTIFICATION_ID

object NotificationUtils {

    private fun aboveOrEqualToAndroidO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun getMainActivityPendingIntent(context: Context) = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getPauseTrackingPendingIntent(context: Context) = PendingIntent.getService(
        context,
        1,
        Intent(context, TrackingService::class.java).also {
            it.action = Constants.ACTION_PAUSE_SERVICE
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getStopTrackingPendingIntent(context: Context) = PendingIntent.getService(
        context,
        2,
        Intent(context, TrackingService::class.java).also {
            it.action = Constants.ACTION_STOP_SERVICE
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getResumeTrackingPendingIntent(context: Context) = PendingIntent.getService(
        context,
        3,
        Intent(context, TrackingService::class.java).also {
            it.action = Constants.ACTION_START_OR_RESUME_SERVICE
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun createTrackingNotification(
        context: Context,
        serviceState: ServiceState,
        notificationDisplayTime: String
    ): Notification {
        val pauseTrackingPendingIntent = getPauseTrackingPendingIntent(context)
        val stopTrackingPendingIntent = getStopTrackingPendingIntent(context)
        val resumeTrackingPendingIntent = getResumeTrackingPendingIntent(context)

        if (aboveOrEqualToAndroidO())
            createNotificationChannel(context)

        val notificationBuilder =
            NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_run)
                .setContentTitle("Run Tracker")
                .setContentText(notificationDisplayTime)
                .setContentIntent(getMainActivityPendingIntent(context))

        if (serviceState == ServiceState.RUNNING) {
            notificationBuilder.addAction(R.drawable.ic_pause, "PAUSE", pauseTrackingPendingIntent)
            notificationBuilder.addAction(R.drawable.ic_stop, "STOP", stopTrackingPendingIntent)
        } else if (serviceState == ServiceState.PAUSED) {
            notificationBuilder.addAction(
                R.drawable.ic_pause,
                "RESUME",
                resumeTrackingPendingIntent
            )
            notificationBuilder.addAction(R.drawable.ic_stop, "STOP", stopTrackingPendingIntent)
        }
        return notificationBuilder.build()
    }

    fun getRunTrackingNotification(context: Context): Notification {
        return createTrackingNotification(context, ServiceState.RUNNING, "00:00:00")
    }

    fun updateNotification(
        context: Context,
        notificationDisplayTime: String,
        serviceState: ServiceState
    ) {
        val notification =
            createTrackingNotification(context, serviceState, notificationDisplayTime)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

}