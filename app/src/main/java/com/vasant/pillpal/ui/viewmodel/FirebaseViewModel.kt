package com.vasant.pillpal.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasant.pillpal.repository.Auth
import com.vasant.pillpal.repository.MedicineRepo
import com.vasant.pillpal.utils.AuthState
import com.vasant.pillpal.utils.FirebaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val auth: Auth,
    private val medicineRepo: MedicineRepo
) : ViewModel() {
    var firebaseState by mutableStateOf<FirebaseState?>(value = null)
    var authState by mutableStateOf<AuthState>(AuthState(Error = null, Success = false))

    fun login(email: String, password: String) {
        viewModelScope.launch {
            firebaseState = FirebaseState.Loading
            authState = auth.SingIn(email, password)
            if (authState.Success) {
                // Sync data with backend on successful login
                viewModelScope.launch {
                    medicineRepo.syncWithBackend()
                }
                firebaseState = FirebaseState.Done
            } else {
                firebaseState = FirebaseState.IsIdle
            }
        }
    }

    fun singUp(email: String, password: String) {
        viewModelScope.launch {
            firebaseState = FirebaseState.Loading
            authState = auth.SingUp(email, password)
            if (authState.Success) {
                // Sync data with backend on successful signup
                viewModelScope.launch {
                    medicineRepo.syncWithBackend()
                }
                firebaseState = FirebaseState.Done
            } else {
                firebaseState = FirebaseState.IsIdle
            }
        }
    }
}