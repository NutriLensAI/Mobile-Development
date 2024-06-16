package com.capstone.mobiledevelopment.nutrilens.view.hasil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.repository.FoodRepository
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodResponse
import kotlinx.coroutines.launch

class HasilMakananViewModel(private val foodRepository: FoodRepository) : ViewModel() {
    private val _nutritions = MutableLiveData<List<FoodResponse>>()
    val nutritions: LiveData<List<FoodResponse>> get() = _nutritions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

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
}