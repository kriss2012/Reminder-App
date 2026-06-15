package com.vasant.pillpal.ui.presentation

import com.vasant.pillpal.data.db.Medicine
data class MedicineState(
    val medicines: List<Medicine> = emptyList(),
    val med_type: MedicineType? =MedicineType.TABLET,
    val isLoading: Boolean=false,
    val medicineName: String = "",
    val isCompleted:Boolean =false,
    val date:Long =0,
    val dosage: String = "",
    val note: String? = null,
)

enum class MedicineType(val displayName: String) {
    TABLET("Tablet"),
    CAPSULE("Capsule"),
    SYRUP("Syrup"),
    DROPS("Drops"),
    OTHERS("Others")
}