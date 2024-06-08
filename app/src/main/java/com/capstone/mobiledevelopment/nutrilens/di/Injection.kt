package com.capstone.mobiledevelopment.nutrilens.di

import android.content.Context
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.pref.SLEEP_PREFERENCES_NAME
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.data.repository.SleepRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository
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
        return StoryRepository.getInstance(apiService)
    }

    fun provideStepCountRepository(context: Context): StepRepository {
        val db = StepDatabase.getDatabase(context)
        val dao = db.stepCountDao()
        return StepRepository.getInstance(dao)
    }

    fun provideSleepCountRepository(context: Context): SleepRepository {
        val db = SleepDatabase.getDatabase(context)
        val event = db.sleepClassifyEventDao()
        val segment = db.sleepSegmentEventDao()
        val sleepSubscriptionStatus = UserPreference.SleepSubscriptionStatus(context.dataStore)
        return SleepRepository.getInstance(sleepSubscriptionStatus, segment, event)
    }
}