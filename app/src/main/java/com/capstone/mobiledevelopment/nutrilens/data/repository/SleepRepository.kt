package com.capstone.mobiledevelopment.nutrilens.data.repository

import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepClassifyEventDao
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepClassifyEventEntity
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepSegmentEventDao
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepSegmentEventEntity
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class SleepRepository(
    private val sleepSubscriptionStatus: UserPreference.SleepSubscriptionStatus,
    private val sleepSegmentEventDao: SleepSegmentEventDao,
    private val sleepClassifyEventDao: SleepClassifyEventDao
) {

    // Methods for SleepSubscriptionStatus
    // Uses [DataStore] to save the subscription to sleep data status. This is used to check if the
    // app is still listening to changes in sleep data when the app is brought back into
    // the foreground.
    val subscribedToSleepDataFlow: Flow<Boolean> = sleepSubscriptionStatus.subscribedToSleepDataFlow

    suspend fun updateSubscribedToSleepData(subscribedToSleepData: Boolean) =
        sleepSubscriptionStatus.updateSubscribedToSleepData(subscribedToSleepData)

    // Methods for SleepSegmentEventDao
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSleepSegmentEvents: Flow<List<SleepSegmentEventEntity>> =
        sleepSegmentEventDao.getAll()

    // By default Room runs suspend queries off the main thread. Therefore, we don't need to
    // implement anything else to ensure we're not doing long-running database work off the
    // main thread.
    suspend fun insertSleepSegment(sleepSegmentEventEntity: SleepSegmentEventEntity) {
        sleepSegmentEventDao.insert(sleepSegmentEventEntity)
    }

    // By default Room runs suspend queries off the main thread. Therefore, we don't need to
    // implement anything else to ensure we're not doing long-running database work off the
    // main thread.
    suspend fun insertSleepSegments(sleepSegmentEventEntities: List<SleepSegmentEventEntity>) {
        sleepSegmentEventDao.insertAll(sleepSegmentEventEntities)
    }

    // Methods for SleepClassifyEventDao
    // Observed Flow will notify the observer when the data has changed.
    val allSleepClassifyEvents: Flow<List<SleepClassifyEventEntity>> =
        sleepClassifyEventDao.getAll()

    suspend fun insertSleepClassifyEvent(sleepClassifyEventEntity: SleepClassifyEventEntity) {
        sleepClassifyEventDao.insert(sleepClassifyEventEntity)
    }

    suspend fun insertSleepClassifyEvents(sleepClassifyEventEntities: List<SleepClassifyEventEntity>) {
        sleepClassifyEventDao.insertAll(sleepClassifyEventEntities)
    }
    companion object {
        @Volatile
        private var instance: SleepRepository? = null

        fun getInstance(
            sleepSubscriptionStatus: UserPreference.SleepSubscriptionStatus,
            sleepSegmentEventDao: SleepSegmentEventDao,
            sleepClassifyEventDao: SleepClassifyEventDao
        ): SleepRepository {
            return instance ?: synchronized(this) {
                instance ?: SleepRepository(sleepSubscriptionStatus, sleepSegmentEventDao, sleepClassifyEventDao).also { instance = it }
            }
        }
    }
}