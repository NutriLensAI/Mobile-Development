package com.capstone.mobiledevelopment.nutrilens.drink

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink")
data class Drink(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Int
)