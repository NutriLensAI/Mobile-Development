package com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MyRecipeDao {
    @Insert
    suspend fun insertRecipe(recipe: MyRecipe)

    @Query("SELECT * FROM my_recipes")
    suspend fun getAllRecipes(): List<MyRecipe>

    @Delete
    suspend fun deleteRecipe(recipe: MyRecipe)  // Add this function
}