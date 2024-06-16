package com.capstone.mobiledevelopment.nutrilens.data.reponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NutritionResponse(
    @field:SerializedName("NutritionResponse")
    val nutritionResponse: List<NutritionResponseItem?>? = null
) : Parcelable

@Parcelize
data class NutritionResponseItem(
    @field:SerializedName("proteins")
    val proteins: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("fat")
    val fat: Double? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("calories")
    val calories: Double? = null,

    @field:SerializedName("carbohydrate")
    val carbohydrate: Double? = null,

    @field:SerializedName("image")
    val image: String? = null
) : Parcelable