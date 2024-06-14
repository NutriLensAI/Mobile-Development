package com.capstone.mobiledevelopment.nutrilens.data.reponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterResponse(

    @field:SerializedName("activity_level")
    val activityLevel: String? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("weight")
    val weight: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("age")
    val age: Int? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("height")
    val height: Int? = null,

    @field:SerializedName("error")
    val error: String? = null
) : Parcelable
