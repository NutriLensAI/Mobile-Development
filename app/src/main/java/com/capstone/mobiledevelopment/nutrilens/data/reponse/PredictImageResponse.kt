package com.capstone.mobiledevelopment.nutrilens.data.reponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PredictImageResponse(

    @field:SerializedName("prediction: ")
    val prediction: String? = null,

    @field:SerializedName("confidence: ")
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