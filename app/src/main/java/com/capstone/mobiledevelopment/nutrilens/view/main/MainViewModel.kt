package com.capstone.mobiledevelopment.nutrilens.view.main

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.reponse.StoriesResponse
import com.capstone.mobiledevelopment.nutrilens.data.repository.StoryRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import com.capstone.mobiledevelopment.nutrilens.view.utils.step.StepCounter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
    private val stepCounter: StepCounter
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storiesResult = MutableLiveData<Result<StoriesResponse>>()
    val storiesResult: LiveData<Result<StoriesResponse>> = _storiesResult

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _steps = MutableLiveData<Long>()
    val steps: LiveData<Long> = _steps

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun fetchToken() {
        viewModelScope.launch {
            val userModel = userRepository.getSession().first()
            _token.value = userModel.token
            getStories()
        }
    }

    fun getStories() {
        val tokenValue = _token.value
        if (!tokenValue.isNullOrEmpty()) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val response = storyRepository.getStories(tokenValue)
                    _storiesResult.value = Result.Success(response)
                } catch (e: Exception) {
                    _storiesResult.value = Result.Failure(e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadTodaySteps(): LiveData<Long> {
        val todaySteps = MutableLiveData<Long>()
        viewModelScope.launch {
            todaySteps.value = userRepository.loadTodaySteps()
        }
        return todaySteps
    }

    fun startStepCounter() {
        viewModelScope.launch {
            stepCounter.stepFlow().collect { stepsCount ->
                _steps.postValue(stepsCount)
            }
        }
    }
}

