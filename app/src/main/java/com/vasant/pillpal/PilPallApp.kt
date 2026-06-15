package com.vasant.pillpal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import com.vasant.pillpal.ui.navigation.NavigationApp
import dagger.hilt.android.HiltAndroidApp

@Composable
fun DoseFlow(windowSizeClass: WindowSizeClass) {
    NavigationApp(windowSizeClass)
}

const val MEDICINE_CHANNEL_ID = "medicine_channel"
const val MEDICINE_CHANNEL_NAME = " DoseFlow  Notifications"

@HiltAndroidApp
class DoseFlowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MEDICINE_CHANNEL_ID,
                MEDICINE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Medicine reminders and alarms"
                enableVibration(true)
            }
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}