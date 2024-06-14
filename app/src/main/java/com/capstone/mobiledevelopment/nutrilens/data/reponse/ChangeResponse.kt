package com.capstone.mobiledevelopment.nutrilens.data.reponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChangeResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: String

) : Parcelable
