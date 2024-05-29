package com.capstone.mobiledevelopment.nutrilens.view.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    fun fetchEmail() {
        viewModelScope.launch {
            val userModel = userRepository.getSession().first()
            _userEmail.value = userModel.email
        }
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
