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

    @Query("""
        SELECT SUM(step_count) as steps, strftime('%Y-%m', date / 1000, 'unixepoch') as month 
        FROM step_counts 
        GROUP BY month
    """)
    fun getMonthlySteps(): LiveData<List<MonthlySteps>>

    data class MonthlySteps(
        val steps: Int,
        val month: String
    )
}

