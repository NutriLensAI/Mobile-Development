package com.capstone.mobiledevelopment.nutrilens.view.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class SignupViewModel( private val userRepository: UserRepository) : ViewModel() {

    private val _registrationResult = MutableLiveData<Result<RegisterResponse>>()
    val registrationResult: LiveData<Result<RegisterResponse>> = _registrationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = userRepository.register(name, email, password)
                _registrationResult.value = Result.Success(response)
            } catch (e: Exception) {
                _registrationResult.value = Result.Failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleRegistrationError(e: Exception) {
        val errorMessage = when (e) {
            is HttpException -> {
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    val json = JSONObject(errorBody.toString())
                    json.getString("message")
                } catch (jsonException: JSONException) {
                    "Registration failed. Please try again."
                }
            }
            else -> "Registration failed. Please try again."
        }
        _registrationResult.value = Result.Failure(Throwable(errorMessage))
        Log.e(TAG, "Registration failed", e)
    }

    companion object {
        private const val TAG = "SignupViewModel"
    }
}