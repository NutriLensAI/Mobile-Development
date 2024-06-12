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

    fun fetchUserPersonalData(token: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.getUserProfile(token)
                _userData.value = PersonalData(
                    activity = response.activityLevel ?: "No data",
                    weight = response.weight?.toString() ?: "No data",
                    height = response.height?.toString() ?: "No data",
                    age = response.age?.toString() ?: "No data",
                    gender = response.gender ?: "No data"
                )
            } catch (e: Exception) {
                Log.e("PersonalViewModel", "Error fetching user personal data", e)
                _userData.value = PersonalData("No data", "No data", "No data", "No data", "No data")
            }
        }
    }

    fun saveUserData(activity: String, weight: String, height: String, age: String, gender: String) {
        _userData.value = PersonalData(activity, weight, height, age, gender)
    }

    fun updateProfileData(token: String, onResult: (Boolean, String) -> Unit) {
        val UserModel = _userData.value ?: return

        viewModelScope.launch {
            try {
                Log.d("PersonalViewModel", "Calling update profile API with token: $token")
                val response = userRepository.updateProfile(
                    token,
                    UserModel.weight.toInt(),
                    UserModel.height.toInt(),
                    UserModel.age.toInt(),
                    UserModel.gender,
                    UserModel.activity
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
