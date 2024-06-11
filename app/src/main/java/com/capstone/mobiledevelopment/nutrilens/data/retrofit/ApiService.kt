package com.capstone.mobiledevelopment.nutrilens.data.retrofit

import com.capstone.mobiledevelopment.nutrilens.data.reponse.Breakfast
import com.capstone.mobiledevelopment.nutrilens.data.reponse.ChangeResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Dinner
import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Lunch
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.UserFoodResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
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

data class UpdatePasswordRequest(
    val newPassword: String
)

data class UpdateProfileRequest(
    val weight: Int,
    val height: Int,
    val age: Int,
    val gender: String,
    val activity_level: String
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

    @GET("nutritions/user/foods")
    suspend fun getAllMeals(
        @Header("Authorization") token: String,
    ): UserFoodResponse

    @PUT("users/profile/editemail")
    suspend fun updateEmail(
        @Header("Authorization") token: String,
        @Body request: UpdateEmailRequest
    ): ChangeResponse

    @PUT("users/profile/editpassword")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): ChangeResponse

    @PUT("users/editprofile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): ChangeResponse
}