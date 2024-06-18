package com.capstone.mobiledevelopment.nutrilens.view.adapter.food

data class FoodResponse(
    val id: Int,
    val calories: Double, // Changed from Int to Double
    val proteins: Double,
    val fat: Double,
    val carbohydrate: Double,
    val name: String,
    val image: String,
    val isRecommended: Boolean = false
)

