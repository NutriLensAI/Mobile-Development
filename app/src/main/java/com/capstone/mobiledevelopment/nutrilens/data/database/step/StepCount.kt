package com.capstone.mobiledevelopment.nutrilens.data.database.step

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "steps")
data class StepCount(
    @ColumnInfo(name = "steps") val steps: Long,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)