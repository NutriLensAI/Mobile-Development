package com.capstone.mobiledevelopment.nutrilens.view.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Macros
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.repository.FoodRepository
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
    private val stepRepository: StepRepository,
    private val foodRepository: FoodRepository // Added to access food data
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _userProfile = MutableLiveData<RegisterResponse>()
    val userProfile: LiveData<RegisterResponse> = _userProfile

    private val _macros = MutableLiveData<Macros?>()
    val macros: LiveData<Macros?> = _macros

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

    fun fetchUsername() {
        viewModelScope.launch {
            val userModel = userRepository.getSession().first()
            _username.value = userModel.username
        }
    }

    fun saveStepCount(stepCount: Int) {
        viewModelScope.launch {
            val today = System.currentTimeMillis()
            stepRepository.saveStepCount(StepCount(stepCount = stepCount, date = today))
        }
    }

    fun fetchUserProfile(token: String) {
        viewModelScope.launch {
            try {
                val userProfile = userRepository.getUserProfile(token)
                _userProfile.postValue(userProfile)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch user profile", e)
            }
        }
    }

    fun fetchMacros(token: String) {
        viewModelScope.launch {
            try {
                val response = foodRepository.getAllMeals(token)
                _macros.postValue(response.macros)
                Log.d(TAG, "Macros fetched successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch macros", e)
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}