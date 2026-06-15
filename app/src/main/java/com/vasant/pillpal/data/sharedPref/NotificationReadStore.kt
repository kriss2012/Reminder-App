package com.vasant.pillpal.data.sharedPref

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationReadStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("notification_reads", Context.MODE_PRIVATE)

    private val _readKeys = MutableStateFlow<Set<String>>(prefs.getStringSet(KEYS, emptySet()) ?: emptySet())
    val readKeys: StateFlow<Set<String>> = _readKeys.asStateFlow()

    fun isRead(key: String): Boolean = _readKeys.value.contains(key)

    fun markRead(key: String) {
        val newSet = _readKeys.value.toMutableSet().apply { add(key) }
        persist(newSet)
    }

    fun markAllRead(keys: Set<String>) {
        val newSet = _readKeys.value.toMutableSet().apply { addAll(keys) }
        persist(newSet)
    }

    private fun persist(set: Set<String>) {
        _readKeys.value = set
        prefs.edit().putStringSet(KEYS, set).apply()
    }

    companion object {
        private const val KEYS = "read_keys"
    }
}

