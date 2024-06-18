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
import io.data2viz.charts.chart.chart
import io.data2viz.charts.chart.discrete
import io.data2viz.charts.chart.mark.bar
import io.data2viz.charts.chart.quantitative
import io.data2viz.viz.VizContainerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                stepRepository.getDailySteps().value ?: emptyList()
            }
            setupChart(view, dailySteps)
        }
    }

    private fun setupChart(view: View, dailySteps: List<StepCountDao.DailySteps>) {
        val stepData = dailySteps.map {
            StepCount(it.day.toInt(), it.steps)
        }

        // Create and configure the chart
        val chartContainer = view.findViewById<FrameLayout>(R.id.chartContainer)
        val vc = VizContainerView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }

        vc.chart(stepData) {
            title = "Monthly Step Count"

            // Create a discrete dimension for the formatted month
            val month = discrete({ domain.formattedMonth }) {
                name = "Month"
            }

            // Create a continuous numeric dimension for the step count
            val steps = quantitative({ domain.stepCount.toDouble() }) {
                name = "Steps Taken"
            }

            // Using a discrete dimension for the X-axis and a continuous one for the Y-axis
            bar(month, steps)
        }

        chartContainer.removeAllViews()
        chartContainer.addView(vc)
    }
}