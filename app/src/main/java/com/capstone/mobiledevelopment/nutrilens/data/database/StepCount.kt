package com.capstone.mobiledevelopment.nutrilens.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_counts")
data class StepCount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "step_count") val stepCount: Int,
    @ColumnInfo(name = "date") val date: Long = System.currentTimeMillis()
)