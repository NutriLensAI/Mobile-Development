package com.capstone.mobiledevelopment.nutrilens.data.repository

import androidx.lifecycle.LiveData
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCountDao

class StepRepository private constructor(
    private val stepCountDao: StepCountDao
) {
    fun getStepCounts(): LiveData<List<StepCount>> = stepCountDao.getAllStepCounts()

    suspend fun saveStepCount(stepCount: StepCount) {
        stepCountDao.insert(stepCount)
    }

    suspend fun getSumStepCounts(start: Long, end: Long): Int {
        return stepCountDao.getSumStepCounts(start, end)
    }

    companion object {
        @Volatile
        private var instance: StepRepository? = null

        fun getInstance(stepCountDao: StepCountDao): StepRepository {
            return instance ?: synchronized(this) {
                instance ?: StepRepository(stepCountDao).also { instance = it }
            }
        }
    }
}