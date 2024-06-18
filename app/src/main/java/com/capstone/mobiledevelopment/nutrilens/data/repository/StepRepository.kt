// StepRepository.kt
package com.capstone.mobiledevelopment.nutrilens.data.repository

import androidx.lifecycle.LiveData
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCountDao

class StepRepository(private val stepCountDao: StepCountDao) {

    fun getStepCounts(): LiveData<List<StepCount>> {
        return stepCountDao.getAllStepCounts()
    }

    suspend fun saveStepCount(stepCount: StepCount) {
        stepCountDao.insert(stepCount)
    }

    fun getWeeklySteps(startOfWeek: Long, endOfWeek: Long): LiveData<List<StepCountDao.WeeklySteps>> {
        return stepCountDao.getWeeklySteps(startOfWeek, endOfWeek)
    }

    suspend fun deleteOldSteps(lastSundayMidnight: Long) {
        stepCountDao.deleteOldSteps(lastSundayMidnight)
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
