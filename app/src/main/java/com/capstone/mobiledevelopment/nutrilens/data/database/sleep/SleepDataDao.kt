package com.capstone.mobiledevelopment.nutrilens.data.database.sleep

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SleepDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleepData: SleepData)

    @Query("SELECT * FROM sleep_data ORDER BY sleepTime DESC")
    suspend fun getAllSleepData(): List<SleepData>
}