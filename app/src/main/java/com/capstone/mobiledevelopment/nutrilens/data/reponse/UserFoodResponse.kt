package com.capstone.mobiledevelopment.nutrilens.data.reponse

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class UserFoodResponse(

	@field:SerializedName("Breakfast")
	val breakfast: Breakfast? = null,

	@field:SerializedName("Dinner")
	val dinner: Dinner? = null,

	@field:SerializedName("Macros")
	val macros: Macros? = null,

	@field:SerializedName("Lunch")
	val lunch: Lunch? = null
) : Parcelable

@Parcelize
data class Total(

	@field:SerializedName("Prot")
	val prot: Int? = null,

	@field:SerializedName("Carbs")
	val carbs: Int? = null,

	@field:SerializedName("Fat")
	val fat: Int? = null,

	@field:SerializedName("Calories")
	val calories: Int? = null
) : Parcelable

@Parcelize
data class DataItem(

	@field:SerializedName("food_name")
	val foodName: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("proteins")
	val proteins: Int? = null,

	@field:SerializedName("fat")
	val fat: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("calories")
	val calories: Int? = null,

	@field:SerializedName("food_id")
	val foodId: Int? = null,

	@field:SerializedName("carbohydrate")
	val carbohydrate: Int? = null
) : Parcelable

@Parcelize
data class Breakfast(

	@field:SerializedName("total")
	val total: Total? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null
) : Parcelable

@Parcelize
data class Macros(

	@field:SerializedName("totalCarbs")
	val totalCarbs: Int? = null,

	@field:SerializedName("totalFat")
	val totalFat: Int? = null,

	@field:SerializedName("totalCalories")
	val totalCalories: Int? = null,

	@field:SerializedName("totalProteins")
	val totalProteins: Int? = null
) : Parcelable

@Parcelize
data class Dinner(

	@field:SerializedName("total")
	val total: Total? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null
) : Parcelable

@Parcelize
data class Lunch(

	@field:SerializedName("total")
	val total: Total? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null
) : Parcelable
