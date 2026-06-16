package com.vasant.pillpal.utils

import android.content.Context

object Prefs {
    private const val PREFS_NAME = "login"
    private const val KEY_IS_LOGGED_IN = "IS_LOGGED_IN"
    private const val KEY_AUTH_TOKEN = "AUTH_TOKEN"
    private const val KEY_USER_EMAIL = "USER_EMAIL"
    private const val KEY_USER_NAME = "USER_NAME"
    private const val KEY_USER_PHONE = "USER_PHONE"
    private const val KEY_USER_DOB = "USER_DOB"
    private const val KEY_USER_GENDER = "USER_GENDER"
    private const val KEY_USER_LOCATION = "USER_LOCATION"

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

    fun getName(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun setName(context: Context, name: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getPhone(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_PHONE, null)
    }

    fun setPhone(context: Context, phone: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USER_PHONE, phone).apply()
    }

    fun getDob(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_DOB, null)
    }

    fun setDob(context: Context, dob: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USER_DOB, dob).apply()
    }

    fun getGender(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_GENDER, null)
    }

    fun setGender(context: Context, gender: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USER_GENDER, gender).apply()
    }

    fun getLocation(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_LOCATION, null)
    }

    fun setLocation(context: Context, location: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USER_LOCATION, location).apply()
    }

    fun clear(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}
