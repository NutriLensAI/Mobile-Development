package com.capstone.mobiledevelopment.nutrilens.view.utils.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepDatabase
import java.util.concurrent.TimeUnit

class SleepWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = SleepDatabase.getDatabase(applicationContext)
        val sleepDataDao = database.sleepDataDao()

        val sleepData = sleepDataDao.getLatestSleepData()

        sleepData?.let {
            val currentTime = System.currentTimeMillis()
            if (currentTime > it.sleepTime) {
                it.sleepCount += 1
                it.sleepTime = currentTime + TimeUnit.HOURS.toMillis(24) // Set next check time
                sleepDataDao.update(sleepData)
            }
        }

        return Result.success()
    }
}