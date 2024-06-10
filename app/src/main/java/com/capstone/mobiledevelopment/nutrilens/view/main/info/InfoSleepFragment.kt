package com.capstone.mobiledevelopment.nutrilens.view.main.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepData
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepDatabase
import com.capstone.mobiledevelopment.nutrilens.view.utils.SleepWorker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class InfoSleepFragment : Fragment() {

    private lateinit var database: SleepDatabase
    private lateinit var scheduleButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info_sleep, container, false)
        database = SleepDatabase.getDatabase(requireContext())
        scheduleButton = view.findViewById(R.id.scheduleButton)

        scheduleButton.setOnClickListener {
            showTimePickerDialog()
        }

        return view
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Select time")
            .build()

        timePicker.show(parentFragmentManager, "TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute
            scheduleSleepWorker(selectedHour, selectedMinute)
        }
    }

    private fun scheduleSleepWorker(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        val sleepTimeInMillis = calendar.timeInMillis

        // Store the sleep time in the database
        lifecycleScope.launch {
            val sleepData = SleepData(sleepTime = sleepTimeInMillis)
            SleepDatabase.getDatabase(requireContext()).sleepDataDao().insert(sleepData)
        }

        // Schedule a periodic worker to check the time and update the count
        val periodicWorkRequest = PeriodicWorkRequestBuilder<SleepWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "SleepWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )

        Toast.makeText(requireContext(), "Sleep time scheduled", Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoSleepFragment()
    }
}