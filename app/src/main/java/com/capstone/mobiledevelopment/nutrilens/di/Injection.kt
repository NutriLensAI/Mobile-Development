package com.capstone.mobiledevelopment.nutrilens.di

import android.content.Context
import android.hardware.SensorManager
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepsDatabase
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.data.repository.StoryRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiConfig
import com.capstone.mobiledevelopment.nutrilens.view.utils.step.StepCounter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val stepsDao = StepsDatabase.getDatabase(context.applicationContext).stepsDao()
        return UserRepository.getInstance(pref, apiService, stepsDao)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService)
    }

    fun provideStepCounter(context: Context): StepCounter {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val userRepository = provideUserRepository(context)
        return StepCounter(sensorManager, userRepository)
    }
}