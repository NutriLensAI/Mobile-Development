package com.capstone.mobiledevelopment.nutrilens.data.retrofit

import com.capstone.mobiledevelopment.nutrilens.data.reponse.PredictImageResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PredictApiService {

    @Multipart
    @POST("/predict-image")
    suspend fun predictImage(
        @Part file: MultipartBody.Part
    ): PredictImageResponse

}