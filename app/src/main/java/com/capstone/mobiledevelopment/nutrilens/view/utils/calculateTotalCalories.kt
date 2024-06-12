package com.capstone.mobiledevelopment.nutrilens.view.utils

import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse

object Utils {
    fun calculateTotalCalories(userProfile: RegisterResponse): Int {
        val weight = userProfile.weight?.toDouble()
        val height = userProfile.height?.toDouble()
        val age = userProfile.age
        val gender = userProfile.gender

        if (weight == null || height == null || age == null || gender.isNullOrBlank()) {
            return 2400
        }

        return if (gender.equals("male", ignoreCase = true)) {
            (66 + (13.7 * weight) + (5 * height) - (6.8 * age)).toInt()
        } else if (gender.equals("female", ignoreCase = true)) {
            (655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)).toInt()
        } else {
            2400
        }
    }
}