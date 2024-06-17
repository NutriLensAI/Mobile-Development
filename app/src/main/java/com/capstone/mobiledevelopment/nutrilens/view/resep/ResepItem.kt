package com.capstone.mobiledevelopment.nutrilens.view.resep

data class ResepItem(
    val Title: String,
    val Ingredients: String,
    val Steps: String
)

data class RecipeData(
    val recipeData: List<ResepItem>
)



