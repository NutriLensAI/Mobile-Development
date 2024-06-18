// StepViewModel.kt
package com.capstone.mobiledevelopment.nutrilens.data.database.step

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class StepViewModel(private val stepRepository: StepRepository) : ViewModel() {
    private val calendar = Calendar.getInstance()

    init {
        // Call the function to delete old steps every Sunday at midnight
        viewModelScope.launch {
            deleteOldSteps()
        }
    }

    fun getWeeklySteps(): LiveData<List<StepCountDao.WeeklySteps>> {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfWeek = calendar.timeInMillis
        return stepRepository.getWeeklySteps(startOfWeek, endOfWeek)
    }

    private suspend fun deleteOldSteps() {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val lastSundayMidnight = calendar.timeInMillis
        stepRepository.deleteOldSteps(lastSundayMidnight)
    }
}

class StepViewModelFactory(private val stepRepository: StepRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StepViewModel(stepRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
