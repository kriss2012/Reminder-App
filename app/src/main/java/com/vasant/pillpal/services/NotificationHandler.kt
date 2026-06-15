package com.vasant.pillpal.services

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.vasant.pillpal.MEDICINE_CHANNEL_ID
import com.vasant.pillpal.R

class NotificationHandler(private val context: Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun createNotification(title: String, message: String, id: Int = 1) {
        val notification = NotificationCompat.Builder(context, MEDICINE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.alarm) // uses existing drawable
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}
