package com.capstone.mobiledevelopment.nutrilens.data.database.step

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StepCount::class], version = 1)
abstract class StepsDatabase : RoomDatabase() {
    abstract fun stepsDao(): StepsDao

    companion object {
        @Volatile
        private var INSTANCE: StepsDatabase? = null

        fun getDatabase(context: Context): StepsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepsDatabase::class.java,
                    "step_count_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}