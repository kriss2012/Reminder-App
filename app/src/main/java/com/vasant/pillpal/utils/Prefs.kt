package com.vasant.pillpal.utils

import android.content.Context

object Prefs {
    private const val PREFS_NAME = "login"
    private const val KEY_IS_LOGGED_IN = "IS_LOGGED_IN"
    private const val KEY_AUTH_TOKEN = "AUTH_TOKEN"
    private const val KEY_USER_EMAIL = "USER_EMAIL"

    fun setLoggedIn(context: Context, isLoggedIn: Boolean, token: String? = null, email: String? = null) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    fun isLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun getEmail(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun clear(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}
