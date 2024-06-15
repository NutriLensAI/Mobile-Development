package com.capstone.mobiledevelopment.nutrilens.data.retrofit

import com.capstone.mobiledevelopment.nutrilens.data.reponse.ChangeResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.UserFoodResponse
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

data class FoodRequest(
    val id: Int,
    val user_id: Int,
    val food_id: Int,
    val food_name: String,
    val calories: Double,
    val proteins: Double,
    val fat: Double,
    val carbohydrate: Double
)

data class UserProfileRequest(
    val weight_kg: Int,
    val height_cm: Int,
    val age_years: Int,
    val gender: String,
    val activity_level: String
)

data class RecommendedFood(
    val name: String,
    val calories: Double,
    val proteins: Double,
    val fat: Double,
    val carbohydrate: Double,
    val image: String? = null, // URL gambar dari API untuk makanan non-rekomendasi
    val isRecommended: Boolean = false // Menandai apakah makanan ini adalah rekomendasi
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

    @GET("nutritions/data")
    suspend fun getFoodData(): List<FoodResponse>

    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): RegisterResponse

    @POST("nutritions/{table}/food/{id}")
    suspend fun addFoodToMeal(
        @Header("Authorization") token: String,
        @Path("table") table: String,
        @Path("id") id: Int,
        @Body request: FoodRequest
    ): FoodResponse

    @POST("show-recommended-foods")
    fun showRecommendedFoods(@Body userProfileRequest: UserProfileRequest): Call<List<RecommendedFood>>
}
