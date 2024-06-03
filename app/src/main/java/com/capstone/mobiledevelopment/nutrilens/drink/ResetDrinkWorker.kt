package com.capstone.mobiledevelopment.nutrilens.drink

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResetDrinkWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val drinkDao = DrinkDatabase.getDatabase(applicationContext).drinkDao()
                drinkDao.resetDrinks()
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}