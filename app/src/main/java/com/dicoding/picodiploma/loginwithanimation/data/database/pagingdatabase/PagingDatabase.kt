package com.dicoding.picodiploma.loginwithanimation.data.database.pagingdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.picodiploma.loginwithanimation.data.database.remotekey.RemoteKeys
import com.dicoding.picodiploma.loginwithanimation.data.database.remotekey.RemoteKeysDao
import com.dicoding.picodiploma.loginwithanimation.data.database.story.StoryDAO
import com.dicoding.picodiploma.loginwithanimation.data.database.story.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class PagingDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDAO
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: PagingDatabase? = null

        fun getDatabase(context: Context): PagingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PagingDatabase::class.java,
                    "story_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}