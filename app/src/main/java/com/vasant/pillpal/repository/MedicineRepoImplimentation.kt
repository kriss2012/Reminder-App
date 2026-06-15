package com.vasant.pillpal.repository

import android.content.Context
import android.util.Log
import com.vasant.pillpal.data.api.DoseFlowApiService
import com.vasant.pillpal.data.api.MedicineDto
import com.vasant.pillpal.data.api.SyncRequest
import com.vasant.pillpal.data.db.Medicine
import com.vasant.pillpal.data.db.dao.MedicineDao
import com.vasant.pillpal.ui.presentation.MedicineType
import com.vasant.pillpal.utils.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MedicineRepoImpl"

class MedicineRepoImplementation @Inject constructor(
    private val dao: MedicineDao,
    private val apiService: DoseFlowApiService,
    private val context: Context
) : MedicineRepo {

    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun addMedicine(medicine: Medicine) {
        // Save locally first
        dao.upsertMedicine(medicine)
        
        // Sync with backend asynchronously
        scope.launch {
            val token = Prefs.getToken(context)
            if (token != null) {
                try {
                    val dto = medicine.toDto()
                    apiService.createMedicine("Bearer $token", dto)
                    Log.d(TAG, "Successfully synced added medicine to backend")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync added medicine to backend: ${e.message}")
                }
            }
        }
    }

    override suspend fun updateMedicine(medicine: Medicine) {
        // Update locally
        dao.upsertMedicine(medicine)

        // Sync with backend
        scope.launch {
            val token = Prefs.getToken(context)
            if (token != null) {
                try {
                    val dto = medicine.toDto()
                    apiService.updateMedicine("Bearer $token", medicine.id, dto)
                    Log.d(TAG, "Successfully synced updated medicine to backend")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync updated medicine to backend: ${e.message}")
                }
            }
        }
    }

    override fun getMedicine(): Flow<List<Medicine>> {
        // Retrieve local flow
        return dao.getMedicineOrderedByName()
    }

    override suspend fun deleteMedicine(medicine: Medicine) {
        // Delete locally
        dao.deleteMedicine(medicine)

        // Sync with backend
        scope.launch {
            val token = Prefs.getToken(context)
            if (token != null) {
                try {
                    apiService.deleteMedicine("Bearer $token", medicine.id)
                    Log.d(TAG, "Successfully synced deleted medicine to backend")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync deleted medicine to backend: ${e.message}")
                }
            }
        }
    }

    override suspend fun syncWithBackend(): Boolean {
        val token = Prefs.getToken(context) ?: return false
        return try {
            val localMeds = getMedicine().first()
            val dtos = localMeds.map { it.toDto() }
            val responseDtos = apiService.syncMedicines("Bearer $token", SyncRequest(dtos))
            
            // Update local Room database with merged results from backend
            for (dto in responseDtos) {
                val med = Medicine(
                    id = dto.id,
                    medName = dto.medName,
                    time = dto.time,
                    dosage = dto.dosage,
                    note = dto.note,
                    isCompleted = dto.isCompleted,
                    medType = dto.medType?.let {
                        try {
                            MedicineType.valueOf(it)
                        } catch (ex: Exception) {
                            MedicineType.TABLET
                        }
                    } ?: MedicineType.TABLET
                )
                dao.upsertMedicine(med)
            }
            Log.d(TAG, "Successfully synchronized local medications with backend")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Sync with backend failed: ${e.message}")
            false
        }
    }

    // Helper conversion extension functions
    private fun Medicine.toDto(): MedicineDto {
        return MedicineDto(
            id = this.id,
            medName = this.medName,
            time = this.time,
            dosage = this.dosage,
            note = this.note,
            isCompleted = this.isCompleted,
            medType = this.medType?.name
        )
    }
}