package com.dicoding.picodiploma.loginwithanimation.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.paging.map
import com.dicoding.picodiploma.loginwithanimation.data.database.pagingdatabase.PagingDatabase
import com.dicoding.picodiploma.loginwithanimation.data.remotemediator.StoryRemoteMediator
import com.dicoding.picodiploma.loginwithanimation.data.reponse.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.reponse.StoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryRepository private constructor(
    private val database: PagingDatabase,
    private var apiService: ApiService
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(database, apiService, token),
            pagingSourceFactory = {
                database.storyDao().getAllStories()
            }
        ).liveData.map { pagingData ->
            pagingData.map { storyEntity ->
                ListStoryItem(
                    id = storyEntity.id,
                    name = storyEntity.name,
                    photoUrl = storyEntity.photoUrl,
                    createdAt = storyEntity.createdAt,
                    description = storyEntity.description,
                    lon = storyEntity.lon,
                    lat = storyEntity.lat
                )
            }
        }
    }

    suspend fun getStoriesResponse(token: String): StoriesResponse {
        return apiService.getAllStories(token)
    }

    suspend fun uploadImage(
        token: String, file: MultipartBody.Part, description: RequestBody,
        lat: Float, lon: Float
    ): StoriesResponse {
        Log.d("StoryRepository", "Uploading image with lat: $lat, lon: $lon")
        val latRequestBody = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lonRequestBody = lon.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        return apiService.uploadStory(token, file, description, latRequestBody, lonRequestBody)
    }

    // Fungsi untuk memperbarui token
    fun updateToken(token: String) {
        apiService = ApiConfig.getApiService(token)
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService,database: PagingDatabase): StoryRepository {
            return synchronized(this) {
                instance ?: StoryRepository(database,apiService).also { instance = it }
            }
        }
    }
}