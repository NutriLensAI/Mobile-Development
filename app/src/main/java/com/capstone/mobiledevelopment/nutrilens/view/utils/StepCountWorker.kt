package com.capstone.mobiledevelopment.nutrilens.view.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiService
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
    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(context.dataStore)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = coroutineScope {
        try {
            val localRecordingClient = FitnessLocal.getLocalRecordingClient(applicationContext)
            val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
            val startTime = endTime.minusWeeks(1)
            val readRequest = LocalDataReadRequest.Builder()
                .aggregate(LocalDataType.TYPE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()

            val response = withContext(Dispatchers.IO) {
                Tasks.await(localRecordingClient.readData(readRequest))
            }
            val totalSteps = response.buckets.flatMap { it.dataSets }.sumOf { dumpDataSet(it) }

            // Save the step count to the user preferences
            saveStepCount(totalSteps)

            // Update the RecyclerView
            updateStepsUI(totalSteps)

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

    private suspend fun saveStepCount(stepCount: Int) {
        userPreference.saveStepCount(stepCount)
    }

    private fun updateStepsUI(stepCount: Int) {
        val intent = Intent("UPDATE_STEP_COUNT")
        intent.putExtra("STEP_COUNT", stepCount)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    companion object {
        private const val TAG = "StepCountWorker"
    }
}