package com.capstone.mobiledevelopment.nutrilens.data.repository

import com.capstone.mobiledevelopment.nutrilens.data.reponse.Breakfast
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Dinner
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Lunch
import com.capstone.mobiledevelopment.nutrilens.data.reponse.UserFoodResponse
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiConfig
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiService

class FoodRepository private constructor(
    private var apiService: ApiService
) {

    suspend fun getAllMeals(token: String): UserFoodResponse {
        return try {
            apiService.getAllMeals(token)
        } catch (e: Exception) {
            throw RuntimeException("Error fetching all meals", e)
        }
    }

    companion object {
        @Volatile
        private var instance: FoodRepository? = null

        fun getInstance(apiService: ApiService): FoodRepository {
            return synchronized(this) {
                instance ?: FoodRepository(apiService).also { instance = it }
            }
        }
    }
}