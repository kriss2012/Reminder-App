package com.vasant.pillpal.data.db

import android.content.Context
import com.vasant.pillpal.ui.presentation.MedicineType


sealed interface MedicineEvent {
    data class PendingMedicine(val data: Medicine) : MedicineEvent
    data class SaveMedicine(val context: Context) : MedicineEvent
    data class MedicineNameChanged(val medicineName: String) : MedicineEvent
    data class AddDosageChange(val dosage: String) : MedicineEvent
    data class DateChanged(val date: Long) : MedicineEvent
    data class DeleteMedicine(val medicine: Medicine) : MedicineEvent
    data class SaveMedicineName(
        val medicineName: String,
        val date: Long,
        val dosage: String,
        val med_type: MedicineType?=null,
        val note: String? = null,
        val isCompleted: Boolean = false
    ) : MedicineEvent

    data class MedicineTypeChanged(val med_type: MedicineType) : MedicineEvent
    data class NoteChanged(val note: String) : MedicineEvent
}