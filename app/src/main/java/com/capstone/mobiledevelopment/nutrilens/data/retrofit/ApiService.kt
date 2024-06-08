package com.capstone.mobiledevelopment.nutrilens.data.retrofit

import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.StoriesResponse
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

    @GET("stories")
    suspend fun getAllStories(
        @Header("Bearer") token: String,
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Bearer") token: String,
        @Part file:  MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): StoriesResponse
}