package com.movielist.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.movielist.R

fun PostNotification(
    context: Context,
    contentTitle: String,
    contentText: String,
    icon: Int = R.drawable.logo_m,
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT
) {
    // Create a notification channel for API 26+
    val channelId = "notification"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelName = "Default Notification Channel"
        val channel = NotificationChannel(
            channelId,
            channelName,
            importance
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Notification builder
    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(contentTitle)
        .setContentText(contentText)
        .setSmallIcon(icon)
        .setOngoing(true)
        .build()

    // Get NotificationManager and post the notification
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(1, notification)
}

