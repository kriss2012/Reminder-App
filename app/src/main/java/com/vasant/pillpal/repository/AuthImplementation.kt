package com.vasant.pillpal.repository

import android.content.Context
import android.util.Log
import com.vasant.pillpal.data.api.AuthRequest
import com.vasant.pillpal.data.api.DoseFlowApiService
import com.vasant.pillpal.utils.AuthState
import com.vasant.pillpal.utils.Prefs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "KiriReminderAuth"

class AuthImplementation @Inject constructor(
    private val apiService: DoseFlowApiService,
    @ApplicationContext private val context: Context
) : Auth {

    override suspend fun SingIn(email: String, password: String): AuthState {
        return try {
            val response = apiService.signIn(AuthRequest(email, password))
            Log.d(TAG, "User successfully logged in via custom backend.")
            
            // Save to Preferences
            Prefs.setLoggedIn(context, true, response.token, response.user.email)

            AuthState(
                Error = null,
                Success = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}")
            AuthState(
                Error = e.message ?: "Authentication failed",
                Success = false
            )
        }
    }

    override suspend fun SingUp(email: String, password: String): AuthState {
        return try {
            val response = apiService.signUp(AuthRequest(email, password))
            Log.d(TAG, "User successfully registered via custom backend.")
            
            // Save to Preferences
            Prefs.setLoggedIn(context, true, response.token, response.user.email)

            AuthState(Error = null, Success = true)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed: ${e.message}")
            AuthState(Error = e.message ?: "Registration failed", Success = false)
        }
    }

    override fun LogOut() {
        Prefs.clear(context)
        Log.d(TAG, "User signed out and cleared token.")
    }
}