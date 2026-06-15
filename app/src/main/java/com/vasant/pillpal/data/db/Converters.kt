package com.vasant.pillpal.data.db

import androidx.room.TypeConverter
import com.vasant.pillpal.ui.presentation.MedicineType

class MedicineTypeConverter {
    @TypeConverter
    fun fromMedicineType(type: MedicineType?): String? = type?.name

    @TypeConverter
    fun toMedicineType(value: String?): MedicineType? = value?.let {
        runCatching { MedicineType.valueOf(it) }.getOrNull()
    }
}

