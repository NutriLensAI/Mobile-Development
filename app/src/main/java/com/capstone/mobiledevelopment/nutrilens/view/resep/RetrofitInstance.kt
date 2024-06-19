package com.capstone.mobiledevelopment.nutrilens.view.resep

import com.capstone.mobiledevelopment.nutrilens.BuildConfig
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}