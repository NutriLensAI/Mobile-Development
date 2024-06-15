package com.capstone.mobiledevelopment.nutrilens.data.reponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class PredictImageResponse(
	@SerializedName("prediction")
	val prediction: String? = null,

	@SerializedName("confidence")
	val confidence: Double? = null,

	@SerializedName("detail")
	val detail: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class ValidationError(
	@SerializedName("loc")
	val loc: @RawValue List<Any>,

	@SerializedName("msg")
	val msg: String,

	@SerializedName("type")
	val type: String
) : Parcelable