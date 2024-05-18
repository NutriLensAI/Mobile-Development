package com.dicoding.picodiploma.loginwithanimation.data.repository

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.reponse.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.reponse.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
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
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository {
            return synchronized(this) {
                instance ?: UserRepository(userPreference, apiService).also { instance = it }
            }
        }
    }
}