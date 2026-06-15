package com.vasant.pillpal.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.vasant.pillpal.data.db.Medicine
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Upsert
    suspend fun upsertMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    @Query("SELECT * FROM medicine ORDER BY medName ASC")
    fun getMedicineOrderedByName(): Flow <List<Medicine>>
}