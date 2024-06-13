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
	val prot: Double? = null,

	@field:SerializedName("Carbs")
	val carbs: Double? = null,

	@field:SerializedName("Fat")
	val fat: Double? = null,

	@field:SerializedName("Calories")
	val calories: Double? = null
) : Parcelable

@Parcelize
data class DataItem(

	@field:SerializedName("food_name")
	val foodName: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("proteins")
	val proteins: Double? = null,

	@field:SerializedName("fat")
	val fat: Double? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("calories")
	val calories: Double? = null,

	@field:SerializedName("food_id")
	val foodId: Int? = null,

	@field:SerializedName("carbohydrate")
	val carbohydrate: Double? = null
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
	val totalCarbs: Double? = null,

	@field:SerializedName("totalFat")
	val totalFat: Double? = null,

	@field:SerializedName("totalCalories")
	val totalCalories: Double? = null,

	@field:SerializedName("totalProteins")
	val totalProteins: Double? = null
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
