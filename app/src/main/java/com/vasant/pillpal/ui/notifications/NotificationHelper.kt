package com.vasant.pillpal.ui.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.vasant.pillpal.MEDICINE_CHANNEL_ID
import com.vasant.pillpal.R
import com.vasant.pillpal.data.db.Medicine
import com.vasant.pillpal.ui.ReminderReceiver
import com.vasant.pillpal.ui.components.REMINDER

object NotificationIds {
    fun forReminder(reminder: Medicine): Int =
        if (reminder.id != 0) reminder.id else (reminder.medName + "_" + reminder.time).hashCode()
}

object NotificationActions {
    const val DONE_ACTION = "DONE_ACTION"
    const val SNOOZE_ACTION = "SNOOZE_ACTION"
    const val DISMISS_ACTION = "DISMISS_ACTION"
}

fun buildReminderNotification(
    context: Context,
    reminder: Medicine,
    channelId: String = MEDICINE_CHANNEL_ID
): NotificationCompat.Builder {
    val reminderJson = Gson().toJson(reminder)

    val doneIntent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, reminderJson)
        action = NotificationActions.DONE_ACTION
    }
    val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, reminderJson)
        action = NotificationActions.SNOOZE_ACTION
    }
    val dismissIntent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, reminderJson)
        action = NotificationActions.DISMISS_ACTION
    }

    val donePending = PendingIntent.getBroadcast(
        context,
        (NotificationActions.DONE_ACTION + reminder.time).hashCode(),
        doneIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val snoozePending = PendingIntent.getBroadcast(
        context,
        (NotificationActions.SNOOZE_ACTION + reminder.time).hashCode(),
        snoozeIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val dismissPending = PendingIntent.getBroadcast(
        context,
        (NotificationActions.DISMISS_ACTION + reminder.time).hashCode(),
        dismissIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val contentIntent = Intent(context, com.vasant.pillpal.MainActivity::class.java)
    val contentPending = PendingIntent.getActivity(
        context,
        NotificationIds.forReminder(reminder),
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.medicine)

    return NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.framemedicine)
        .setLargeIcon(largeIcon)
        .setContentTitle("Medicine Reminder")
        .setContentText("${reminder.medName} is due now \u2022 Take ${reminder.dosage}")
        .setStyle(
            NotificationCompat.BigTextStyle().bigText(
                "It's time to take ${reminder.medName}. Dosage: ${reminder.dosage}" +
                        (reminder.note?.let { "\nNote: $it" } ?: "")
            )
        )
        .setContentIntent(contentPending)
        .addAction(R.drawable.potion, "Mark as taken", donePending)
        .addAction(R.drawable.guide, "Snooze 10 min", snoozePending)
        .addAction(R.drawable.arrow, "Dismiss", dismissPending)
        .setAutoCancel(true)
        .setOnlyAlertOnce(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setColor(ContextCompat.getColor(context, R.color.purple_200))
        .setColorized(true)
}

fun showReminderNotification(context: Context, reminder: Medicine, channelId: String = MEDICINE_CHANNEL_ID) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) return
    }
    val notification = buildReminderNotification(context, reminder, channelId).build()
    NotificationManagerCompat.from(context).notify(
        NotificationIds.forReminder(reminder),
        notification
    )
}

fun cancelReminderNotification(context: Context, reminder: Medicine) {
    NotificationManagerCompat.from(context).cancel(NotificationIds.forReminder(reminder))
}
