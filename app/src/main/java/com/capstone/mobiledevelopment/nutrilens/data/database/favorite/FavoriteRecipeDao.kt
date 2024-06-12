package com.capstone.mobiledevelopment.nutrilens.data.database.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteRecipeDao {
    @Insert
    suspend fun insertFavorite(recipe: FavoriteRecipe)

    @Query("SELECT * FROM favorite_recipes")
    suspend fun getAllFavorites(): List<FavoriteRecipe>

    @Query("SELECT * FROM favorite_recipes WHERE title = :title LIMIT 1")
    suspend fun getFavoriteByTitle(title: String): FavoriteRecipe?

    @Query("DELETE FROM favorite_recipes WHERE title = :title")
    suspend fun removeFavoriteByTitle(title: String)
}
