package com.capstone.mobiledevelopment.nutrilens.data.repository

import com.capstone.mobiledevelopment.nutrilens.data.reponse.StoriesResponse
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiConfig
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private var apiService: ApiService
) {

    suspend fun getStories(token: String): StoriesResponse {
        return apiService.getAllStories(token)
    }

    suspend fun uploadImage(token: String, file: MultipartBody.Part, description: RequestBody): StoriesResponse {
        return apiService.uploadStory(token, file, description)
    }

    // Fungsi untuk memperbarui token
    fun updateToken(token: String) {
        apiService = ApiConfig.getApiService(token)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository {
            return synchronized(this) {
                instance ?: StoryRepository(apiService).also { instance = it }
            }
        }
    }
}