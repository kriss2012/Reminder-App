package com.vasant.pillpal.utils

import com.google.firebase.auth.FirebaseUser

sealed class FirebaseState {
    object IsIdle : FirebaseState()
    object Loading : FirebaseState()
    object Done : FirebaseState()
}


data class AuthState(
    val Error:String?=null,
    val Success: Boolean,
    )


/*
    1.I have no  idea why i wrote this
    2.i dont know how tot use this or implement this

 */
sealed class AuthResult {
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}