package com.capstone.mobiledevelopment.nutrilens.view.settings.email

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EmailViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    fun updateEmail(newEmail: String, token: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("EmailViewModel", "Calling update email API with token: $token")
                val response = userRepository.updateEmail(token, newEmail)
                Log.d("EmailViewModel", "API Response: ${response.message}")
                onResult(response.message == "Email updated successfully", response.message.toString())
            } catch (e: HttpException) {
                Log.e("EmailViewModel", "HTTP Error updating email: ${e.message()}", e)
                onResult(false, "HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                Log.e("EmailViewModel", "Error updating email", e)
                onResult(false, "Error updating email")
            }
        }
    }
}