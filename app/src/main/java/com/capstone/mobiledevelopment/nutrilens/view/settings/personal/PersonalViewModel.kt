package com.capstone.mobiledevelopment.nutrilens.view.settings.personal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonalViewModel : ViewModel() {
    private val _activityLevel = MutableLiveData<String>()
    val activityLevel: LiveData<String> get() = _activityLevel

    fun setActivityLevel(level: String) {
        _activityLevel.value = level
    }
}