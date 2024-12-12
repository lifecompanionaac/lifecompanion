package org.lifecompanion.phonecontrolapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import org.lifecompanion.phonecontrolapp.R

object Notify {
    fun createNotificationChannel(name: String, channelId: String, context:Context) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, name, importance)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification(title: String, channelId: String, context: Context): Notification {
        val icon = R.mipmap.icon
        return Notification.Builder(context, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .build()
    }
}
