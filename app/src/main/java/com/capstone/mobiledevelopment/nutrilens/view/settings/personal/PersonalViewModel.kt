package com.capstone.mobiledevelopment.nutrilens.view.settings.personal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PersonalViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userData = MutableLiveData<PersonalData>()
    val userData: LiveData<PersonalData> get() = _userData

    fun saveUserData(activity: String, weight: String, height: String, age: String, gender: String) {
        _userData.value = PersonalData(activity, weight, height, age, gender)
    }

    fun updateProfileData(token: String, onResult: (Boolean, String) -> Unit) {
        val userData = _userData.value ?: return

        viewModelScope.launch {
            try {
                Log.d("PersonalViewModel", "Calling update profile API with token: $token")
                val response = userRepository.updateProfile(
                    token,
                    userData.weight.toInt(),
                    userData.height.toInt(),
                    userData.age.toInt(),
                    userData.gender,
                    userData.activity
                )
                Log.d("PersonalViewModel", "API Response: ${response.message}")
                onResult(response.message == "Profile updated successfully", response.message)
            } catch (e: HttpException) {
                Log.e("PersonalViewModel", "HTTP Error updating profile: ${e.message()}", e)
                onResult(false, "HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                Log.e("PersonalViewModel", "Error updating profile", e)
                onResult(false, "Error updating profile")
            }
        }
    }
}
