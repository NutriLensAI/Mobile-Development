package com.capstone.mobiledevelopment.nutrilens.data.database.step

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.MyRecipe
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.MyRecipeDao
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipe
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipeDao

@Database(entities = [StepCount::class, FavoriteRecipe::class, MyRecipe::class], version = 2, exportSchema = false)
abstract class StepDatabase : RoomDatabase() {
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
    abstract fun myRecipeDao(): MyRecipeDao
    abstract fun stepCountDao(): StepCountDao

    companion object {
        @Volatile
        private var INSTANCE: StepDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Migration code if necessary
                // e.g., database.execSQL("ALTER TABLE my_recipes ADD COLUMN new_column INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): StepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepDatabase::class.java,
                    "nutrilens_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
