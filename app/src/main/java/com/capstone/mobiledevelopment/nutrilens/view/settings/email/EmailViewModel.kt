package com.capstone.mobiledevelopment.nutrilens.view.settings.email

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EmailViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun updateEmail(token: String, newEmail: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userRepository.updateEmail(token, newEmail)
                Log.d("EmailViewModel", "Response: ${response.message}")
                onResult(response.message == "Email updated successfully")
            } catch (e: HttpException) {
                Log.e("EmailViewModel", "HTTP Error updating email: ${e.message()}", e)
                onResult(false)
            } catch (e: Exception) {
                Log.e("EmailViewModel", "Error updating email", e)
                onResult(false)
            }
        }
    }
}
