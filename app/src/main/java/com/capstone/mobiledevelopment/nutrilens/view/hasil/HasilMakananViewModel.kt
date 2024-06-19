package com.capstone.mobiledevelopment.nutrilens.view.hasil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.BuildConfig
import com.capstone.mobiledevelopment.nutrilens.data.repository.FoodRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.FoodRequest
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodResponse
import com.capstone.mobiledevelopment.nutrilens.view.resep.RecipeData
import com.capstone.mobiledevelopment.nutrilens.view.resep.ResepItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

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

    private val _recipes = MutableLiveData<List<ResepItem>>()
    val recipes: LiveData<List<ResepItem>> get() = _recipes

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
                _nutritions.value = emptyList()
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
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchRecipes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val recipes = withContext(Dispatchers.IO) { getRecipeDataFromUrl() }
                _recipes.postValue(recipes)
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getRecipeDataFromUrl(): List<ResepItem> {
        val urlString = BuildConfig.API_BASE_URL
        var jsonString: String
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            jsonString = connection.inputStream.bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }

        val gson = Gson()
        val recipeType = object : TypeToken<RecipeData>() {}.type
        val recipeData: RecipeData = gson.fromJson(jsonString, recipeType)
        return recipeData.recipeData
    }
}
