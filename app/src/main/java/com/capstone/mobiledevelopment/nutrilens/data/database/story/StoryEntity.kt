package com.capstone.mobiledevelopment.nutrilens.data.database.story

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val description: String? = null,
    val lon: Double? = null,
    val lat: Double? = null
) : Parcelable