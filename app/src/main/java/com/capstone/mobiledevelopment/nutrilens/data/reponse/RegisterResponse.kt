package com.capstone.mobiledevelopment.nutrilens.data.reponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterResponse(
	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("username")
	val username: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("weight")
	val weight: Int,

	@field:SerializedName("height")
	val height: Int,

	@field:SerializedName("age")
	val age: Int,

	@field:SerializedName("gender")
	val gender: String,

	@field:SerializedName("activity_level")
	val activityLevel: String
) : Parcelable
