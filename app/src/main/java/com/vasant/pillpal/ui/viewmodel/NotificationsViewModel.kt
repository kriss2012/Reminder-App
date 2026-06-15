package com.vasant.pillpal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasant.pillpal.data.db.Medicine
import com.vasant.pillpal.data.sharedPref.NotificationReadStore
import com.vasant.pillpal.repository.MedicineRepo
import com.vasant.pillpal.ui.notifications.NotificationItem
import com.vasant.pillpal.ui.notifications.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repo: MedicineRepo,
    private val readStore: NotificationReadStore
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())

    val items: StateFlow<List<NotificationItem>> = combine(
        repo.getMedicine(),
        readStore.readKeys
    ) { meds, reads ->
        buildNotifications(meds, reads)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private fun buildNotifications(medicines: List<Medicine>, readKeys: Set<String>): List<NotificationItem> {
        val now = System.currentTimeMillis()
        val list = medicines.map { med ->
            val type = when {
                med.isCompleted -> NotificationType.COMPLETED
                med.time < now -> NotificationType.MISSED
                else -> NotificationType.REMINDER
            }
            val key = keyFor(med, type)
            NotificationItem(
                key = key,
                title = when (type) {
                    NotificationType.REMINDER -> "Upcoming reminder"
                    NotificationType.MISSED -> "Missed medication"
                    NotificationType.COMPLETED -> "Medication completed"
                    NotificationType.SYSTEM -> "System update"
                },
                message = when (type) {
                    NotificationType.REMINDER -> "${med.medName} due at ${dateFormatter.format(Date(med.time))}"
                    NotificationType.MISSED -> "You missed ${med.medName} at ${dateFormatter.format(Date(med.time))}"
                    NotificationType.COMPLETED -> "Great job! You completed ${med.medName}"
                    NotificationType.SYSTEM -> ""
                },
                timeText = relativeTime(now, med.time),
                timeMillis = med.time,
                type = type,
                isRead = readKeys.contains(key)
            )
        }
        return list.sortedByDescending { it.timeMillis }
    }

    fun markAllRead() {
        viewModelScope.launch {
            val keys = items.value.map { it.key }.toSet()
            readStore.markAllRead(keys)
        }
    }

    fun markRead(key: String) {
        viewModelScope.launch { readStore.markRead(key) }
    }

    private fun keyFor(med: Medicine, type: NotificationType): String =
        "${'$'}{med.id}:${'$'}{med.medName}:${'$'}{med.time}:${'$'}type"

    private fun relativeTime(now: Long, time: Long): String {
        val diff = now - time
        val abs = kotlin.math.abs(diff)
        val minute = 60_000L
        val hour = 60 * minute
        val day = 24 * hour
        return when {
            abs < minute -> if (diff >= 0) "just now" else "in a moment"
            abs < hour -> {
                val m = (abs / minute).toInt()
                if (diff >= 0) "${'$'}m min ago" else "in ${'$'}m min"
            }
            abs < day -> {
                val h = (abs / hour).toInt()
                if (diff >= 0) "${'$'}h hr ago" else "in ${'$'}h hr"
            }
            else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(time))
        }
    }
}

