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
class NotificationPrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)

    private val _enabled = MutableStateFlow(prefs.getBoolean(KEY_ENABLED, true))
    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_ENABLED, true))
    private val _vibration = MutableStateFlow(prefs.getBoolean(KEY_VIBRATION, false))
    private val _soundUri = MutableStateFlow(prefs.getString(KEY_SOUND_URI, null))

    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()
    val vibration: StateFlow<Boolean> = _vibration.asStateFlow()
    val soundUri: StateFlow<String?> = _soundUri.asStateFlow()

    // Synchronous getters for places where flows are inconvenient (e.g., BroadcastReceiver)
    fun isEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, true)
    fun isSoundEnabled(): Boolean = prefs.getBoolean(KEY_SOUND_ENABLED, true)
    fun isVibrationEnabled(): Boolean = prefs.getBoolean(KEY_VIBRATION, false)
    fun getSoundUri(): String? = prefs.getString(KEY_SOUND_URI, null)

    fun setEnabled(value: Boolean) {
        _enabled.value = value
        prefs.edit().putBoolean(KEY_ENABLED, value).apply()
    }

    fun setSoundEnabled(value: Boolean) {
        _soundEnabled.value = value
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()
    }

    fun setVibration(value: Boolean) {
        _vibration.value = value
        prefs.edit().putBoolean(KEY_VIBRATION, value).apply()
    }

    fun setSoundUri(uri: String?) {
        _soundUri.value = uri
        prefs.edit().putString(KEY_SOUND_URI, uri).apply()
    }

    companion object {
        private const val KEY_ENABLED = "enabled"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION = "vibration"
        private const val KEY_SOUND_URI = "sound_uri"
    }
}
