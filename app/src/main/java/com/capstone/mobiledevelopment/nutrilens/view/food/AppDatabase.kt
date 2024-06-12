package com.capstone.mobiledevelopment.nutrilens.view.food

import androidx.room.Database
import androidx.room.RoomDatabase
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipe
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipeDao

@Database(entities = [FavoriteRecipe::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
}

