package com.capstone.mobiledevelopment.nutrilens.view.hasil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.repository.FoodRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.FoodRequest
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HasilMakananViewModel(
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nutritions = MutableLiveData<List<FoodResponse>>()
    val nutritions: LiveData<List<FoodResponse>> get() = _nutritions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    private val _addFoodResult = MutableLiveData<FoodResponse>()
    val addFoodResult: LiveData<FoodResponse> get() = _addFoodResult

    init {
        fetchToken()
    }

    fun fetchToken() {
        viewModelScope.launch {
            val userModel = userRepository.getSession().first()
            _token.value = userModel.token
        }
    }

    fun fetchNutritions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = foodRepository.getNutritions()
                _nutritions.value = response
            } catch (e: Exception) {
                // Handle the error
                _nutritions.value = emptyList() // Optionally, set an empty list on error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFoodToMeal(token: String, table: String, id: Int, foodRequest: FoodRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = foodRepository.addFoodToMeal(token, table, id, foodRequest)
                _addFoodResult.value = response
            } catch (e: Exception) {
                // Handle the error
            } finally {
                _isLoading.value = false
            }
        }
    }
}