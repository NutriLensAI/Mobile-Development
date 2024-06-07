package com.capstone.mobiledevelopment.nutrilens.view.adapter.resep

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteRecipe::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
}
