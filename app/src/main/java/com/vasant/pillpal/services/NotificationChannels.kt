package com.vasant.pillpal.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri

private const val BASE_ID = "medicine"

fun ensureChannelForSettings(
    context: Context,
    soundEnabled: Boolean,
    vibration: Boolean,
    soundUriString: String?
): String {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return "${BASE_ID}_legacy"

    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channelId = when {
        !soundEnabled && !vibration -> "${BASE_ID}_silent"
        soundUriString.isNullOrBlank() && soundEnabled -> if (vibration) "${BASE_ID}_default_vib" else "${BASE_ID}_default"
        else -> {
            val hash = soundUriString?.hashCode() ?: 0
            if (vibration) "${BASE_ID}_custom_${hash}_vib" else "${BASE_ID}_custom_${hash}"
        }
    }

    // If channel exists, return it
    nm.getNotificationChannel(channelId)?.let { return channelId }

    val name = when {
        channelId.contains("silent") -> "Medicine (Silent)"
        channelId.contains("custom") -> "Medicine (Custom)"
        channelId.contains("default") -> "Medicine (Default)"
        else -> "Medicine"
    }

    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(channelId, name, importance).apply {
        description = "Medicine reminders and alarms"
        enableVibration(vibration)
        if (!soundEnabled) {
            // Explicitly silence the channel when sound is disabled
            setSound(null, null)
        } else {
            // If a custom sound is selected, apply it; otherwise use system default by not calling setSound
            soundUriString?.toUri()?.let { customUri: Uri ->
                val audioAttrs = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setSound(customUri, audioAttrs)
            }
        }
    }
    nm.createNotificationChannel(channel)
    return channelId
}
