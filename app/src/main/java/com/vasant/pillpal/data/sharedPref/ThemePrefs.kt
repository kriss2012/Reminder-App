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
class ThemePrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(prefs.getString(KEY_THEME, "whisker") ?: "whisker")
    val theme: StateFlow<String> = _theme.asStateFlow()

    fun getTheme(): String = prefs.getString(KEY_THEME, "whisker") ?: "whisker"

    fun setTheme(value: String) {
        _theme.value = value
        prefs.edit().putString(KEY_THEME, value).apply()
    }

    companion object {
        private const val KEY_THEME = "app_theme"
    }
}
