package com.capstone.mobiledevelopment.nutrilens.data.reponse

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class NutritionResponse(

	@field:SerializedName("NutritionResponse")
	val nutritionResponse: List<NutritionResponseItem?>? = null
) : Parcelable

@Parcelize
data class NutritionResponseItem(

	@field:SerializedName("proteins")
	val proteins: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("fat")
	val fat: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("calories")
	val calories: Int? = null,

	@field:SerializedName("carbohydrate")
	val carbohydrate: Int? = null
) : Parcelable
