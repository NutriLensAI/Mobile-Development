package com.capstone.mobiledevelopment.nutrilens.data.database.drink

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DrinkDao {
    @Insert
    suspend fun insert(drink: Drink)

    @Query("SELECT SUM(amount) FROM drink")
    suspend fun getTotalAmount(): Int?

    @Query("SELECT SUM(sugar) FROM drink")
    suspend fun getTotalSugarAmount(): Int?

    @Query("DELETE FROM drink")
    suspend fun resetDrinks()

    @Query("SELECT * FROM drink")
    suspend fun getAllDrinks(): List<Drink>
}