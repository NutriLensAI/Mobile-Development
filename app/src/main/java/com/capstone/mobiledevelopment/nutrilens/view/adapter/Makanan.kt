package com.capstone.mobiledevelopment.nutrilens.view.adapter

import android.os.Parcel
import android.os.Parcelable

data class Makanan(
    val nama: String,
    val calories: Int,
    val carbs: Int,
    val fat: Int,
    val protein: Int,
    val carbsPercentage: String,
    val fatPercentage: String,
    val proteinPercentage: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nama)
        parcel.writeInt(calories)
        parcel.writeInt(carbs)
        parcel.writeInt(fat)
        parcel.writeInt(protein)
        parcel.writeString(carbsPercentage)
        parcel.writeString(fatPercentage)
        parcel.writeString(proteinPercentage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Makanan> {
        override fun createFromParcel(parcel: Parcel): Makanan {
            return Makanan(parcel)
        }

        override fun newArray(size: Int): Array<Makanan?> {
            return arrayOfNulls(size)
        }
    }
}
