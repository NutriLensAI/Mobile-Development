package com.capstone.mobiledevelopment.nutrilens.di

import android.content.Context
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.data.repository.FoodRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiConfig
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.PredictApiConfig

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideFoodRepository(context: Context): FoodRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val predictApiService = PredictApiConfig.getApiService()
        return FoodRepository.getInstance(apiService, predictApiService)
    }

    fun provideStepCountRepository(context: Context): StepRepository {
        val db = StepDatabase.getDatabase(context)
        val dao = db.stepCountDao()
        return StepRepository.getInstance(dao)
    }
}