package com.capstone.mobiledevelopment.nutrilens.data.database.sleep

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_data")
data class SleepData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var sleepTime: Long,
    var sleepCount: Int = 0
)