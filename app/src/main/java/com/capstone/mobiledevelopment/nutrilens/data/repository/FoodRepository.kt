package com.capstone.mobiledevelopment.nutrilens.data.repository

import com.capstone.mobiledevelopment.nutrilens.data.reponse.PredictImageResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.UserFoodResponse
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiService
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.PredictApiService
import okhttp3.MultipartBody

class FoodRepository private constructor(
    private var apiService: ApiService,
    private var predictApiService: PredictApiService
) {

    suspend fun getAllMeals(token: String): UserFoodResponse {
        return try {
            apiService.getAllMeals(token)
        } catch (e: Exception) {
            throw RuntimeException("Error fetching all meals", e)
        }
    }

    suspend fun predictImage(file: MultipartBody.Part): PredictImageResponse {
        return try {
            predictApiService.predictImage(file)
        } catch (e: Exception) {
            throw RuntimeException("Error predicting image", e)
        }
    }
    companion object {
        @Volatile
        private var instance: FoodRepository? = null

        fun getInstance(apiService: ApiService, predictApiService: PredictApiService): FoodRepository {
            return synchronized(this) {
                instance ?: FoodRepository(apiService, predictApiService).also { instance = it }
            }
        }
    }
}