package com.capstone.mobiledevelopment.nutrilens.view.catatan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.UserFoodResponse
import com.capstone.mobiledevelopment.nutrilens.data.repository.FoodRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.Utils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CatatanMakananViewModel(
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _allMeals = MutableLiveData<UserFoodResponse>()
    val allMeals: LiveData<UserFoodResponse> = _allMeals

    private val _totalCalories = MutableLiveData<Int>()
    val totalCalories: LiveData<Int> = _totalCalories

    fun fetchToken() {
        viewModelScope.launch {
            val userModel = userRepository.getSession().first()
            _token.value = userModel.token
        }
    }

    fun fetchAllMeals() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val tokenValue = _token.value ?: throw IllegalStateException("Token is null")
                val userProfile = userRepository.getUserProfile(tokenValue)
                val meals = foodRepository.getAllMeals(tokenValue)
                _totalCalories.postValue(Utils.calculateTotalCalories(userProfile))
                _allMeals.postValue(meals)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}