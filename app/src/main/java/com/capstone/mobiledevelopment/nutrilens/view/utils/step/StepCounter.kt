package com.capstone.mobiledevelopment.nutrilens.view.utils.step

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepsDatabase
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class StepCounter(
    private val sensorManager: SensorManager,
    private val userRepository: UserRepository
) {
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val scope = CoroutineScope(Dispatchers.IO)

    fun stepFlow(): Flow<Long> = callbackFlow {
        if (stepSensor == null) {
            Log.e("StepCounter", "Step Counter Sensor is not available")
            close(Throwable("Step Counter Sensor is not available"))
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                    val steps = event.values[0].toLong()
                    trySend(steps).isSuccess

                    // Save steps to database using UserRepository
                    scope.launch {
                        userRepository.storeSteps(steps)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not used in this context
            }
        }

        stepSensor?.also {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_FASTEST)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}