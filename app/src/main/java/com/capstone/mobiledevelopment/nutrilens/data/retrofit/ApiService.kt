package com.capstone.mobiledevelopment.nutrilens.data.retrofit

import com.capstone.mobiledevelopment.nutrilens.data.reponse.ChangeResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

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

interface ApiService {

    @POST("users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @PUT("users/profile/editemail")
    suspend fun updateEmail(
        @Header("Authorization") token: String,
        @Body request: UpdateEmailRequest
    ): ChangeResponse
}