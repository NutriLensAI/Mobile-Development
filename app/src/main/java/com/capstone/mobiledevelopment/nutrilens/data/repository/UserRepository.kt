package com.capstone.mobiledevelopment.nutrilens.data.repository

import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.reponse.ChangeResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiConfig
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiService
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.LoginRequest
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.RegisterRequest
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.UpdateEmailRequest
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private var userPreference: UserPreference,
    private var apiService: ApiService
) {
    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun updateToken(token: String) {
        apiService = ApiConfig.getApiService(token)
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        val request = RegisterRequest(username = name, email = email, password = password)
        return apiService.register(request)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val request = LoginRequest(email = email, password = password)
        return apiService.login(request)
    }

    suspend fun updateEmail(token: String, email: String): ChangeResponse {
        val request = UpdateEmailRequest(email = email)
        return apiService.updateEmail(token, request)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService).also { instance = it }
            }
        }
    }
}