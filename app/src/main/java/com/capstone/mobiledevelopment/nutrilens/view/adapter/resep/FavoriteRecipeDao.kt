package com.capstone.mobiledevelopment.nutrilens.view.adapter.resep

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteRecipeDao {
    @Insert
    suspend fun insertFavorite(recipe: FavoriteRecipe)

    @Query("SELECT * FROM favorite_recipes")
    suspend fun getAllFavorites(): List<FavoriteRecipe>
}
