package com.capstone.mobiledevelopment.nutrilens.view.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepClassifyEventEntity
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepSegmentEventEntity
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.reponse.StoriesResponse
import com.capstone.mobiledevelopment.nutrilens.data.repository.SleepRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.StoryRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
    private val sleepRepository: SleepRepository,
    private val stepRepository: StepRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storiesResult = MutableLiveData<Result<StoriesResponse>>()
    val storiesResult: LiveData<Result<StoriesResponse>> = _storiesResult

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

    fun saveStepCount(stepCount: Int) {
        viewModelScope.launch {
            val today = System.currentTimeMillis()
            stepRepository.saveStepCount(StepCount(stepCount = stepCount, date = today))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val subscribedToSleepDataLiveData = sleepRepository.subscribedToSleepDataFlow.asLiveData()

    fun updateSubscribedToSleepData(subscribed: Boolean) = viewModelScope.launch {
        sleepRepository.updateSubscribedToSleepData(subscribed)
    }

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSleepSegments: LiveData<List<SleepSegmentEventEntity>> =
        sleepRepository.allSleepSegmentEvents.asLiveData()

    val allSleepClassifyEventEntities: LiveData<List<SleepClassifyEventEntity>> =
        sleepRepository.allSleepClassifyEvents.asLiveData()
}
