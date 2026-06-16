package com.vasant.pillpal.repository

import com.vasant.pillpal.utils.AuthState

interface Auth {
    suspend fun SingIn(email: String, password: String): AuthState
    suspend fun SingUp(email: String, password: String): AuthState
    suspend fun updatePassword(email: String, oldPass: String, newPass: String): AuthState
    fun LogOut()
}