package com.capstone.mobiledevelopment.nutrilens.data.database.step

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StepsDao {
    @Query("SELECT * FROM steps")
    suspend fun getAll(): List<StepCount>

    @Query("SELECT * FROM steps WHERE created_at >= date(:startDateTime) " +
            "AND created_at < date(:startDateTime, '+1 day')")
    suspend fun loadAllStepsFromToday(startDateTime: String): Array<StepCount>

    @Insert
    suspend fun insertAll(vararg steps: StepCount)

    @Delete
    suspend fun delete(steps: StepCount)
}