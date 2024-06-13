package com.capstone.mobiledevelopment.nutrilens.data.database.drink

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink")
data class Drink(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Int,
    val sugar: Int = 0
)