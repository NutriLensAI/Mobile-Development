package com.capstone.mobiledevelopment.nutrilens.di

import android.content.Context
import com.capstone.mobiledevelopment.nutrilens.data.database.pagingdatabase.PagingDatabase
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.data.repository.StoryRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val database = PagingDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService,database)
    }
}