package com.capstone.mobiledevelopment.nutrilens.data.database.step

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_counts")
data class StepCount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "step_count") val stepCount: Int,
    @ColumnInfo(name = "date") val date: Long = System.currentTimeMillis()
) {
    val formattedMonth: String
        get() = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).format(java.util.Date(date))
}

