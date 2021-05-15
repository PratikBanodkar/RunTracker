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
import com.appedia.runtracker.ui.MainActivity

object NotificationUtils {

        private fun aboveOrEqualToAndroidO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNotificationChannel(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        fun getRunTrackingNotification(context: Context): Notification {
            if(aboveOrEqualToAndroidO())
                createNotificationChannel(context)
            val notificationBuilder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_run)
                .setContentTitle("Run Tracker")
                .setContentText("00:00:00")
                .setContentIntent(getMainActivityPendingIntent(context))
            return notificationBuilder.build()
        }

        private fun getMainActivityPendingIntent(context: Context) = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).also {
                it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )


}