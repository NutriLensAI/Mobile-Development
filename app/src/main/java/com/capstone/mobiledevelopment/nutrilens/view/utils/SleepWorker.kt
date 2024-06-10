package com.capstone.mobiledevelopment.nutrilens.view.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepData
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepDatabase

class SleepWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val sleepTime = inputData.getLong("SLEEP_TIME", 0L)
        val database = SleepDatabase.getDatabase(applicationContext)
        val sleepDataDao = database.sleepDataDao()

        // Save sleep time to database
        sleepDataDao.insert(SleepData(sleepTime = sleepTime))

        return Result.success()
    }
}
