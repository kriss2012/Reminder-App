package com.vasant.pillpal.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vasant.pillpal.ui.presentation.MedicineType

@Entity
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medName: String,
    val time: Long,
    val dosage: String,
    val note: String? = null,
    val isCompleted: Boolean = false,
    val medType: MedicineType? = null
)
