package com.capstone.mobiledevelopment.nutrilens.view.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.capstone.mobiledevelopment.nutrilens.di.Injection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class StepCountWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    private val userRepository = Injection.provideUserRepository(context)
    private val scope = CoroutineScope(Dispatchers.IO)  // Create a coroutine scope using IO dispatcher

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = coroutineScope {
        try {
            val sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            if (sensor == null) {
                Log.e(TAG, "Step counter sensor is not available on this device.")
                return@coroutineScope Result.failure()
            }

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return

                    val stepsSinceLastReboot = event.values[0].toLong()
                    Log.d(TAG, "Steps since last reboot: $stepsSinceLastReboot")

                    scope.launch {
                        userRepository.storeSteps(stepsSinceLastReboot)
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    Log.d(TAG, "Accuracy changed to: $accuracy")
                }
            }

            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
            Thread.sleep(10000)  // Keep the listener active for a short period
            sensorManager.unregisterListener(listener)  // Unregister the listener

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading fitness data", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "StepCountWorker"
    }
}