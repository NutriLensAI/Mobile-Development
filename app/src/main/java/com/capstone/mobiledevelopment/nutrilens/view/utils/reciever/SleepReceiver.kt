package com.capstone.mobiledevelopment.nutrilens.view.utils.reciever

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepClassifyEventEntity
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepSegmentEventEntity
import com.capstone.mobiledevelopment.nutrilens.data.repository.SleepRepository
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Saves Sleep Events to Database.
 */
class SleepReceiver : BroadcastReceiver() {

    // Used to launch coroutines (non-blocking way to insert data).
    private val scope: CoroutineScope = MainScope()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive(): $intent")

        val repository: SleepRepository = (context.applicationContext as MainApplication).repository

        // TODO: Extract sleep information from PendingIntent.
        if (SleepSegmentEvent.hasEvents(intent)) {
            val sleepSegmentEvents: List<SleepSegmentEvent> =
                SleepSegmentEvent.extractEvents(intent)
            Log.d(TAG, "SleepSegmentEvent List: $sleepSegmentEvents")
            addSleepSegmentEventsToDatabase(repository, sleepSegmentEvents)
        } else if (SleepClassifyEvent.hasEvents(intent)) {
            val sleepClassifyEvents: List<SleepClassifyEvent> =
                SleepClassifyEvent.extractEvents(intent)
            Log.d(TAG, "SleepClassifyEvent List: $sleepClassifyEvents")
            addSleepClassifyEventsToDatabase(repository, sleepClassifyEvents)
        }
    }

    private fun addSleepSegmentEventsToDatabase(
        repository: SleepRepository,
        sleepSegmentEvents: List<SleepSegmentEvent>
    ) {
        if (sleepSegmentEvents.isNotEmpty()) {
            scope.launch {
                val convertedToEntityVersion: List<SleepSegmentEventEntity> =
                    sleepSegmentEvents.map {
                        SleepSegmentEventEntity.from(it)
                    }
                repository.insertSleepSegments(convertedToEntityVersion)
            }
        }
    }

    private fun addSleepClassifyEventsToDatabase(
        repository: SleepRepository,
        sleepClassifyEvents: List<SleepClassifyEvent>
    ) {
        if (sleepClassifyEvents.isNotEmpty()) {
            scope.launch {
                val convertedToEntityVersion: List<SleepClassifyEventEntity> =
                    sleepClassifyEvents.map {
                        SleepClassifyEventEntity.from(it)
                    }
                repository.insertSleepClassifyEvents(convertedToEntityVersion)
            }
        }
    }

    companion object {
        const val TAG = "SleepReceiver"
        fun createSleepReceiverPendingIntent(context: Context): PendingIntent {
            val sleepIntent = Intent(context, SleepReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                0,
                sleepIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}