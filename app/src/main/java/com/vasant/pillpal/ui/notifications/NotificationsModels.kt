package com.vasant.pillpal.ui.notifications

import androidx.compose.ui.graphics.Color

data class NotificationItem(
    val key: String,
    val title: String,
    val message: String,
    val timeText: String,
    val timeMillis: Long,
    val type: NotificationType,
    val isRead: Boolean
)

enum class NotificationType { REMINDER, MISSED, COMPLETED, SYSTEM }

