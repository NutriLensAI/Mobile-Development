package com.capstone.mobiledevelopment.nutrilens.view.resep.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val ingredients: String,
    val steps: String
)

