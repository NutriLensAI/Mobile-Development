package com.capstone.mobiledevelopment.nutrilens.data.repository

import androidx.lifecycle.LiveData
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepClassifyEventDao
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepClassifyEventEntity
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepSegmentEventDao
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepSegmentEventEntity
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCountDao
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class StepRepository private constructor(
    private val stepCountDao: StepCountDao,
) {
    fun getStepCounts(): LiveData<List<StepCount>> = stepCountDao.getAllStepCounts()

    suspend fun saveStepCount(stepCount: StepCount) {
        stepCountDao.insert(stepCount)
    }

    companion object {
        @Volatile
        private var instance: StepRepository? = null

        fun getInstance(
            stepCountDao: StepCountDao,
        ): StepRepository {
            return instance ?: synchronized(this) {
                instance ?: StepRepository(stepCountDao).also { instance = it }
            }
        }
    }
}