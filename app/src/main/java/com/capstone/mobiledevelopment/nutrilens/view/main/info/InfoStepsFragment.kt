package com.capstone.mobiledevelopment.nutrilens.view.main.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCountDao
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.customview.StepChart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InfoStepsFragment : Fragment() {

    private lateinit var stepRepository: StepRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_steps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

        // Initialize the database and repository
        val stepCountDao = Room.databaseBuilder(
            requireContext(),
            StepDatabase::class.java,
            "step_database"
        ).build().stepCountDao()

        stepRepository = StepRepository.getInstance(stepCountDao)

        // Load the data and update the chart
        loadStepData(view)
    }

    private fun setupView() {
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
                controller.isAppearanceLightStatusBars = true // Optional: Set status bar content to dark
            }
            activity?.actionBar?.hide()
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green2)
        }
    }

    private fun loadStepData(view: View) {
        lifecycleScope.launch {
            val dailySteps = withContext(Dispatchers.IO) {
                stepRepository.getDailySteps().value
            }
            if (dailySteps.isNullOrEmpty()) {
                // Use placeholder data
                val placeholderData = listOf(
                    StepCount(0, 3000, System.currentTimeMillis() - 86400000 * 6), // 6 days ago
                    StepCount(0, 5000, System.currentTimeMillis() - 86400000 * 5), // 5 days ago
                    StepCount(0, 7000, System.currentTimeMillis() - 86400000 * 4), // 4 days ago
                    StepCount(0, 6000, System.currentTimeMillis() - 86400000 * 3), // 3 days ago
                    StepCount(0, 8000, System.currentTimeMillis() - 86400000 * 2), // 2 days ago
                    StepCount(0, 4000, System.currentTimeMillis() - 86400000 * 1), // 1 day ago
                    StepCount(0, 9000, System.currentTimeMillis()) // today
                )
                loadStepChart(view, placeholderData.map {
                    StepCountDao.DailySteps(it.stepCount, SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                        Date(it.date)
                    ))
                })
            } else {
                loadStepChart(view, dailySteps)
            }
        }
    }

    private fun loadStepChart(view: View, dailySteps: List<StepCountDao.DailySteps>) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Create and add the chart to the container
        val stepChart = StepChart(requireContext(), dailySteps.map {
            val date = dateFormat.parse(it.day)?.time ?: 0L
            StepCount(0, it.steps, date)
        })
        view.findViewById<FrameLayout>(R.id.chartContainer).removeAllViews()
        view.findViewById<FrameLayout>(R.id.chartContainer).addView(stepChart)
    }
}