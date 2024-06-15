package com.capstone.mobiledevelopment.nutrilens.view.hasil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.reponse.NutritionResponseItem
import com.capstone.mobiledevelopment.nutrilens.data.repository.FoodRepository
import kotlinx.coroutines.launch

class HasilMakananViewModel(private val foodRepository: FoodRepository) : ViewModel() {
    private val _nutritions = MutableLiveData<List<NutritionResponseItem>>()
    val nutritions: LiveData<List<NutritionResponseItem>> get() = _nutritions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchNutritions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = foodRepository.getNutritions()
                _nutritions.value = response.nutritionResponse?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                // Handle the error
            } finally {
                _isLoading.value = false
            }
        }
    }
}