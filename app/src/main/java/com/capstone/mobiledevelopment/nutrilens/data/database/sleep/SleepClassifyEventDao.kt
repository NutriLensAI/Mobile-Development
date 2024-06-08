package com.capstone.mobiledevelopment.nutrilens.data.database.sleep

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepClassifyEventDao {
    @Query("SELECT * FROM sleep_classify_events_table ORDER BY time_stamp_seconds DESC")
    fun getAll(): Flow<List<SleepClassifyEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleepClassifyEventEntity: SleepClassifyEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sleepClassifyEventEntities: List<SleepClassifyEventEntity>)

    @Delete
    suspend fun delete(sleepClassifyEventEntity: SleepClassifyEventEntity)

    @Query("DELETE FROM sleep_classify_events_table")
    suspend fun deleteAll()
}