package com.capstone.mobiledevelopment.nutrilens.view.adapter.resep

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCountDao
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipe
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipeDao

@Database(entities = [StepCount::class, FavoriteRecipe::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
    abstract fun stepCountDao(): StepCountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutrilens_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

