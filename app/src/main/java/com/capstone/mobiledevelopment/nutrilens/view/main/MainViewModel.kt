package com.capstone.mobiledevelopment.nutrilens.view.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

class MainViewModel(
    private val userRepository: UserRepository,
    private val stepRepository: StepRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    val stepCounts: LiveData<List<StepCount>> = stepRepository.getStepCounts()

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun fetchToken() {
        viewModelScope.launch {
            val userModel = userRepository.getSession().first()
            _token.value = userModel.token
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getTotalStepsForToday(): LiveData<Int> {
        val today = System.currentTimeMillis()
        val startOfDay = LocalDateTime.ofInstant(Instant.ofEpochMilli(today), ZoneId.systemDefault())
            .with(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC) * 1000
        val endOfDay = LocalDateTime.ofInstant(Instant.ofEpochMilli(today), ZoneId.systemDefault())
            .with(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC) * 1000

        val totalStepsLiveData = MutableLiveData<Int>()
        viewModelScope.launch {
            val totalSteps = stepRepository.getSumStepCounts(startOfDay, endOfDay)
            totalStepsLiveData.postValue(totalSteps)
        }
        return totalStepsLiveData
    }

    fun saveStepCount(stepCount: Int) {
        viewModelScope.launch {
            val today = System.currentTimeMillis()
            stepRepository.saveStepCount(StepCount(stepCount = stepCount, date = today))
        }
    }
}