package com.vasant.pillpal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vasant.pillpal.data.db.dao.MedicineDao

@Database(
    entities = [Medicine::class], version = 3, exportSchema = true
)
@TypeConverters(MedicineTypeConverter::class)
abstract class MedicineDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
}