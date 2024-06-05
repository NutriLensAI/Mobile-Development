package com.capstone.mobiledevelopment.nutrilens.view.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.capstone.mobiledevelopment.nutrilens.data.database.step.AppDatabase
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository
import com.google.android.gms.fitness.FitnessLocal
import com.google.android.gms.fitness.data.LocalDataSet
import com.google.android.gms.fitness.data.LocalDataType
import com.google.android.gms.fitness.request.LocalDataReadRequest
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class StepCountWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    private val db by lazy { AppDatabase.getDatabase(applicationContext) }
    private val stepRepository by lazy { StepRepository.getInstance(db.stepCountDao()) }
    private val sharedPreferences by lazy {
        applicationContext.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = coroutineScope {
        try {
            val localRecordingClient = FitnessLocal.getLocalRecordingClient(applicationContext)
            val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
            val startTime = endTime.minusDays(1)  // Changed to read only the last day's data
            val readRequest = LocalDataReadRequest.Builder()
                .aggregate(LocalDataType.TYPE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()

            val response = withContext(Dispatchers.IO) {
                Tasks.await(localRecordingClient.readData(readRequest))
            }
            val totalSteps = response.buckets.flatMap { it.dataSets }.sumOf { dumpDataSet(it) }

            // Save the step count to the database
            val lastSavedSteps = sharedPreferences.getInt("lastSavedSteps", 0)
            val newSteps = totalSteps - lastSavedSteps
            if (newSteps > 0) {
                val stepCount = StepCount(stepCount = newSteps, date = System.currentTimeMillis())
                stepRepository.saveStepCount(stepCount)
                sharedPreferences.edit().putInt("lastSavedSteps", totalSteps).apply()
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading fitness data", e)
            Result.failure()
        }
    }

    private fun dumpDataSet(dataSet: LocalDataSet): Int {
        var stepCount = 0
        for (dp in dataSet.dataPoints) {
            for (field in dp.dataType.fields) {
                stepCount += dp.getValue(field).asInt()
            }
        }
        return stepCount
    }
}
