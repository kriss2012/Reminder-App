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
            // Save locally for offline fallback
            val sharedPreferences = context.getSharedPreferences("local_accounts", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(email, password).apply()

            AuthState(
                Error = null,
                Success = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}. Checking local backup.")
            val sharedPreferences = context.getSharedPreferences("local_accounts", Context.MODE_PRIVATE)
            val savedPassword = sharedPreferences.getString(email, null)
            if (savedPassword != null && savedPassword == password) {
                Log.d(TAG, "User successfully logged in offline (local fallback).")
                Prefs.setLoggedIn(context, true, "local-token-123456", email)
                AuthState(
                    Error = null,
                    Success = true
                )
            } else {
                AuthState(
                    Error = "Incorrect credentials or account not found",
                    Success = false
                )
            }
        }
    }

    override suspend fun SingUp(email: String, password: String): AuthState {
        return try {
            val response = apiService.signUp(AuthRequest(email, password))
            Log.d(TAG, "User successfully registered via custom backend.")
            
            // Save to Preferences
            Prefs.setLoggedIn(context, true, response.token, response.user.email)
            val sharedPreferences = context.getSharedPreferences("local_accounts", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(email, password).apply()

            AuthState(Error = null, Success = true)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed on backend: ${e.message}. Creating account locally.")
            val sharedPreferences = context.getSharedPreferences("local_accounts", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(email, password).apply()
            
            Prefs.setLoggedIn(context, true, "local-token-123456", email)
            AuthState(Error = null, Success = true)
        }
    }

    override suspend fun updatePassword(email: String, oldPass: String, newPass: String): AuthState {
        val sharedPreferences = context.getSharedPreferences("local_accounts", Context.MODE_PRIVATE)
        val savedPassword = sharedPreferences.getString(email, null)
        if (savedPassword != null && savedPassword != oldPass) {
            return AuthState(Error = "Current password does not match", Success = false)
        }
        sharedPreferences.edit().putString(email, newPass).apply()
        return AuthState(Error = null, Success = true)
    }

    override fun LogOut() {
        Prefs.clear(context)
        Log.d(TAG, "User signed out and cleared token.")
    }
}