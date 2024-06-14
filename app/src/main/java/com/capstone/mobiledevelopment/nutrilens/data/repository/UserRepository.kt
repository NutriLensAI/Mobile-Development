package com.capstone.mobiledevelopment.nutrilens.data.repository

import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.reponse.ChangeResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiService
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.LoginRequest
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.RegisterRequest
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.UpdateEmailRequest
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.UpdatePasswordRequest
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.UpdateProfileRequest
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

    suspend fun updatePassword(token: String, newPassword: String): ChangeResponse {
        return apiService.updatePassword(token, UpdatePasswordRequest(newPassword))
    }

    suspend fun updateProfile(
        token: String,
        weight: Int,
        height: Int,
        age: Int,
        gender: String,
        activityLevel: String
    ): ChangeResponse {
        val request = UpdateProfileRequest(weight, height, age, gender, activityLevel)
        return apiService.updateProfile(token, request)
    }

    suspend fun getUserProfile(token: String): RegisterResponse {
        return apiService.getUserProfile(token)
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