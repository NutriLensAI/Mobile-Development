package com.capstone.mobiledevelopment.nutrilens.view.main.info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepData
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepDatabase
import com.capstone.mobiledevelopment.nutrilens.view.utils.worker.SleepWorker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

class InfoSleepFragment : Fragment() {

    private lateinit var database: SleepDatabase
    private lateinit var scheduleButton: Button
    private lateinit var clockTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info_sleep, container, false)
        database = SleepDatabase.getDatabase(requireContext())
        scheduleButton = view.findViewById(R.id.scheduleButton)
        clockTextView = view.findViewById(R.id.clockTextView)

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
            saveSleepTime(selectedHour, selectedMinute)
            scheduleSleepWorker(selectedHour, selectedMinute)
            calculateAndDisplayTotalSleepDuration()
        }
    }

    private fun saveSleepTime(hour: Int, minute: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("SleepPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("HOUR", hour)
            putInt("MINUTE", minute)
            apply()
        }
    }

    private fun loadSleepTime(): Pair<Int, Int> {
        val sharedPreferences = requireContext().getSharedPreferences("SleepPrefs", Context.MODE_PRIVATE)
        val hour = sharedPreferences.getInt("HOUR", -1)
        val minute = sharedPreferences.getInt("MINUTE", -1)
        return Pair(hour, minute)
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
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )

        Toast.makeText(requireContext(), "Sleep time scheduled", Toast.LENGTH_SHORT).show()

        // Update the clockTextView to show the selected time
        val timeText = String.format("%02d:%02d", hour, minute)
        clockTextView.text = "Sleep time set: $timeText"
    }

    private fun calculateTotalSleepDuration(setHour: Int, setMinute: Int, sleepCount: Int): Long {
        val setSleepTimeInMillis = (setHour * 60 * 60 * 1000 + setMinute * 60 * 1000).toLong()
        val sleepDurationPerDay = 24 * 60 * 60 * 1000 - setSleepTimeInMillis
        return sleepDurationPerDay * sleepCount
    }

    private fun calculateAndDisplayTotalSleepDuration() {
        val (setHour, setMinute) = loadSleepTime()
        if (setHour != -1 && setMinute != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                val sleepDataDao = SleepDatabase.getDatabase(requireContext()).sleepDataDao()
                val sleepCount = sleepDataDao.getTotalSleepCount() ?: 0
                val totalSleepDuration = calculateTotalSleepDuration(setHour, setMinute, sleepCount)
                withContext(Dispatchers.Main) {
                    val hours = totalSleepDuration / (60 * 60 * 1000)
                    val minutes = (totalSleepDuration % (60 * 60 * 1000)) / (60 * 1000)
                    val timeText = String.format("%02d:%02d", setHour, setMinute)
                    clockTextView.text = getString(
                        R.string.sleep_time_set_total_sleep_duration_h_m,
                        timeText,
                        hours,
                        minutes
                    )
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoSleepFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        calculateAndDisplayTotalSleepDuration()
    }

    private fun setupView() {
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
                controller.isAppearanceLightStatusBars = true // Set status bar content to dark
                controller.isAppearanceLightNavigationBars =
                    true // Set navigation bar content to dark
            }
            activity?.actionBar?.hide()
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green)
            window.navigationBarColor = ContextCompat.getColor(
                requireContext(),
                R.color.white
            ) // Change navigation bar color
        }
    }
}