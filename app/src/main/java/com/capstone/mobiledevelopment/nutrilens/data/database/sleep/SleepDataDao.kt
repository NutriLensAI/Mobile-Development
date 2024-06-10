package com.capstone.mobiledevelopment.nutrilens.data.database.sleep

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SleepDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleepData: SleepData)

    @Update
    suspend fun update(sleepData: SleepData)

    @Query("SELECT * FROM sleep_data ORDER BY sleepTime DESC LIMIT 1")
    suspend fun getLatestSleepData(): SleepData?

    @Query("SELECT SUM(sleepCount) FROM sleep_data")
    suspend fun getTotalSleepCount(): Int
}