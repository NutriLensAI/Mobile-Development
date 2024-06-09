package com.capstone.mobiledevelopment.nutrilens.data.repository

import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiConfig
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiService

class FoodRepository private constructor(
    private var apiService: ApiService
) {

    // Fungsi untuk memperbarui token
    fun updateToken(token: String) {
        apiService = ApiConfig.getApiService(token)
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