package com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>
)

fun loadRecipesFromAssets(context: Context): List<Recipe> {
    val jsonString: String
    try {
        jsonString = context.assets.open("datarecipe.json").bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return emptyList()
    }

    val listRecipeType = object : TypeToken<List<Recipe>>() {}.type
    return Gson().fromJson(jsonString, listRecipeType)
}
