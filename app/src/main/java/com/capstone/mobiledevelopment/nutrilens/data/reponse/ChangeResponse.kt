package com.capstone.mobiledevelopment.nutrilens.data.reponse

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ChangeResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("error")
	val error: String

) : Parcelable
