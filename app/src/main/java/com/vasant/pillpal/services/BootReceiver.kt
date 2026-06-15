package com.vasant.pillpal.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d("BootReceiver", "Device rebooted. TODO: Reschedule medication alarms.")
            // TODO: Query repository and reschedule pending alarms
        }
    }
}
