package com.capstone.mobiledevelopment.nutrilens.view.settings.personal

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PersonalViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    // Live data for different user attributes
    private val _userData = MutableLiveData<PersonalData>()

    val userData: LiveData<PersonalData> get() = _userData

    init {
        loadUserData()
    }

    fun saveUserData(weight: String, height: String, age: String, gender: String) {
        _userData.value = PersonalData(weight, height, age, gender)
        sharedPreferences.edit().apply {
            putString("weight", weight)
            putString("height", height)
            putString("age", age)
            putString("gender", gender)
            apply()
        }
    }

    private fun loadUserData() {
        val weight = sharedPreferences.getString("weight", "Not set")
        val height = sharedPreferences.getString("height", "Not set")
        val age = sharedPreferences.getString("age", "Not set")
        val gender = sharedPreferences.getString("gender", "Not set")
        _userData.value = PersonalData(weight!!, height!!, age!!, gender!!)
    }
}