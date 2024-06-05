package com.capstone.mobiledevelopment.nutrilens.data.database.step

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepCountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepCount: StepCount)

    @Query("SELECT * FROM step_counts ORDER BY date DESC")
    fun getAllStepCounts(): LiveData<List<StepCount>>

    @Query("SELECT SUM(step_count) FROM step_counts WHERE date >= :start AND date <= :end")
    suspend fun getSumStepCounts(start: Long, end: Long): Int
}