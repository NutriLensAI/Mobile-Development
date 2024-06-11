package com.capstone.mobiledevelopment.nutrilens.view.settings.password

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PasswordViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    fun updatePassword(newPassword: String, token: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("PasswordViewModel", "Calling update password API with token: $token")
                val response = userRepository.updatePassword(token, newPassword)
                Log.d("PasswordViewModel", "API Response: ${response.message}")
                onResult(response.message == "Password updated successfully", response.message)
            } catch (e: HttpException) {
                Log.e("PasswordViewModel", "HTTP Error updating password: ${e.message()}", e)
                onResult(false, "HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                Log.e("PasswordViewModel", "Error updating password", e)
                onResult(false, "Error updating password")
            }
        }
    }
}
