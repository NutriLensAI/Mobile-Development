package com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_recipes")
data class MyRecipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "ingredients") val ingredients: String,
    @ColumnInfo(name = "steps") val steps: String
)

