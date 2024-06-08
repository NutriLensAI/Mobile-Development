package com.capstone.mobiledevelopment.nutrilens.view.utils.reciever

import android.app.Application
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.data.repository.SleepRepository


class MainApplication : Application() {
    private val database by lazy {
        SleepDatabase.getDatabase(applicationContext)
    }

    val repository by lazy {
        SleepRepository.getInstance(
            sleepSubscriptionStatus = UserPreference.SleepSubscriptionStatus(
                applicationContext.dataStore
            ),
            sleepSegmentEventDao = database.sleepSegmentEventDao(),
            sleepClassifyEventDao = database.sleepClassifyEventDao()
        )
    }
}