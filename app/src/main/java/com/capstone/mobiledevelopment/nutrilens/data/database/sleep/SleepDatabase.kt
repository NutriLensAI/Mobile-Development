package com.capstone.mobiledevelopment.nutrilens.data.database.sleep

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SleepData::class], version = 2)
abstract class SleepDatabase : RoomDatabase() {
    abstract fun sleepDataDao(): SleepDataDao

    companion object {
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Perform the necessary SQL commands to migrate the schema
                database.execSQL("ALTER TABLE sleep_data ADD COLUMN sleepCount INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): SleepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SleepDatabase::class.java,
                    "sleep_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}