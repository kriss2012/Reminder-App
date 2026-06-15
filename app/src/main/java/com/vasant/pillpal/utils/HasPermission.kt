package com.vasant.pillpal.utils

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat


const val ALARM_PERMISSION = "SCHEDULE_EXACT_ALARM"
const val NOTIFICATION_PERMISSION = "POST_NOTIFICATIONS"
fun hasPermission(context: Context, nameOfPermission: String): Boolean {
    return when (nameOfPermission) {
        ALARM_PERMISSION -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
                alarmManager?.canScheduleExactAlarms() ?: false
            } else {
                true
            }
        }

        NOTIFICATION_PERMISSION -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }

        else -> {
            false
        }
    }
}