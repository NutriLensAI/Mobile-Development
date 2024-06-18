// InfoStepsFragment.kt
package com.capstone.mobiledevelopment.nutrilens.view.main.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCountDao
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepViewModel
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.customview.StepChart
import java.text.SimpleDateFormat
import java.util.Locale

class InfoStepsFragment : Fragment() {

    private lateinit var stepRepository: StepRepository
    private lateinit var stepViewModel: StepViewModel

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
        stepViewModel = ViewModelProvider(this, StepViewModelFactory(stepRepository)).get(StepViewModel::class.java)

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
        stepViewModel.getWeeklySteps().observe(viewLifecycleOwner, Observer { weeklySteps ->
            loadStepChart(view, weeklySteps)
        })
    }

    private fun loadStepChart(view: View, weeklySteps: List<StepCountDao.WeeklySteps>) {
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        val stepChart = StepChart(requireContext(), weeklySteps.map {
            val dayOfWeek = when (it.day) {
                "0" -> "Sunday"
                "1" -> "Monday"
                "2" -> "Tuesday"
                "3" -> "Wednesday"
                "4" -> "Thursday"
                "5" -> "Friday"
                "6" -> "Saturday"
                else -> "Unknown"
            }
            StepCount(0, it.steps, dayOfWeekFormat.parse(dayOfWeek)?.time ?: 0L)
        })
        view.findViewById<FrameLayout>(R.id.chartContainer).removeAllViews()
        view.findViewById<FrameLayout>(R.id.chartContainer).addView(stepChart)
    }
}
