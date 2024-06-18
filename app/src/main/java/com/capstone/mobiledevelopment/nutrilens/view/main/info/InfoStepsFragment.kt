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
        return inflater.inflate(R.layout.fragment_info_steps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

        val stepCountDao = Room.databaseBuilder(
            requireContext(),
            StepDatabase::class.java,
            "step_database"
        ).build().stepCountDao()

        stepRepository = StepRepository.getInstance(stepCountDao)

        loadStepData(view)
    }

    private fun setupView() {
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
                controller.isAppearanceLightStatusBars = true
            }
            activity?.actionBar?.hide()
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green2)
        }
    }

    private fun loadStepData(view: View) {
        lifecycleScope.launch {
            val monthlySteps = withContext(Dispatchers.IO) {
                stepRepository.getMonthlySteps().value
            }
            if (monthlySteps.isNullOrEmpty()) {
                val placeholderData = listOf(
                    StepCountDao.MonthlySteps(9000, "2023-01"),
                    StepCountDao.MonthlySteps(8000, "2023-02"),
                    StepCountDao.MonthlySteps(10000, "2023-03"),
                    StepCountDao.MonthlySteps(7000, "2023-04"),
                    StepCountDao.MonthlySteps(8500, "2023-05"),
                    StepCountDao.MonthlySteps(9500, "2023-06"),
                    StepCountDao.MonthlySteps(11000, "2023-07"),
                    StepCountDao.MonthlySteps(12000, "2023-08"),
                    StepCountDao.MonthlySteps(11500, "2023-09"),
                    StepCountDao.MonthlySteps(10500, "2023-10"),
                    StepCountDao.MonthlySteps(9800, "2023-11"),
                    StepCountDao.MonthlySteps(10200, "2023-12")
                )
                loadStepChart(view, placeholderData)
            } else {
                loadStepChart(view, monthlySteps)
            }
        }
    }

    private fun loadStepChart(view: View, monthlySteps: List<StepCountDao.MonthlySteps>) {
        val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())

        val stepChart = StepChart(requireContext(), monthlySteps.map {
            StepCount(0, it.steps, dateFormat.parse(it.month)?.time ?: 0L)
        })
        view.findViewById<FrameLayout>(R.id.chartContainer).removeAllViews()
        view.findViewById<FrameLayout>(R.id.chartContainer).addView(stepChart)
    }
}
