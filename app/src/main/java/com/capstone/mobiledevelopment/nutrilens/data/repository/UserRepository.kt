package com.capstone.mobiledevelopment.nutrilens.data.repository

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepsDao
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val stepsDao: StepsDao
) {

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun storeSteps(stepsSinceLastReboot: Long) = withContext(Dispatchers.IO) {
        val stepCount = StepCount(
            steps = stepsSinceLastReboot,
            createdAt = Instant.now().toString()
        )
        Log.d(TAG, "Storing steps: $stepCount")
        stepsDao.insertAll(stepCount)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadTodaySteps(): Long = withContext(Dispatchers.IO) {
        val todayAtMidnight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).toString()
        val todayDataPoints = stepsDao.loadAllStepsFromToday(startDateTime = todayAtMidnight)
        return@withContext when {
            todayDataPoints.isEmpty() -> 0
            else -> {
                val firstDataPointOfTheDay = todayDataPoints.first()
                val latestDataPointSoFar = todayDataPoints.last()

                val todaySteps = latestDataPointSoFar.steps - firstDataPointOfTheDay.steps
                Log.d(TAG, "Today Steps: $todaySteps")
                todaySteps
            }
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService, stepsDao: StepsDao): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService, stepsDao).also { instance = it }
            }
        }
    }
}