package com.vasant.pillpal.repository

import com.vasant.pillpal.data.db.Medicine
import kotlinx.coroutines.flow.Flow

interface MedicineRepo {
    suspend fun addMedicine(medicine: Medicine)
    suspend fun updateMedicine(medicine: Medicine)
    fun getMedicine(): Flow<List<Medicine>>
    suspend fun deleteMedicine(medicine: Medicine)
    suspend fun syncWithBackend(): Boolean
}