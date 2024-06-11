package com.capstone.mobiledevelopment.nutrilens.data.retrofit

import androidx.room.Query
import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UpdateEmailRequest(
    val email: String
)

data class ApiResponse(
    val message: String
)

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @Headers("Content-Type: application/json")
    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @Headers("Content-Type: application/json")
    @PUT("users/profile/editemail")
    suspend fun updateEmail(
        @Header("Authorization") token: String,
        @Body request: UpdateEmailRequest
    ): ApiResponse
}