// StepViewModel.kt
package com.capstone.mobiledevelopment.nutrilens.data.database.step

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository

class StepViewModel(private val stepRepository: StepRepository) : ViewModel() {
    val monthlySteps: LiveData<List<StepCountDao.MonthlySteps>> = stepRepository.getMonthlySteps()
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
