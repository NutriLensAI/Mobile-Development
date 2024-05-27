package com.capstone.mobiledevelopment.nutrilens.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.repository.StoryRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class LoginViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }

    fun updateToken(token: String) {
        storyRepository.updateToken(token)
    }

    fun login(email: String, password: String) {
        _isLoading.value = true // Start loading
        viewModelScope.launch {
            try {
                Log.d(TAG, "Login started")
                val response = userRepository.login(email, password)
                _loginResult.value = Result.Success(response)
                updateToken(response.loginResult?.token ?: "") // Update token in StoryRepository
                Log.d(TAG, "Login successful")
            } catch (e: Exception) {
                handleLoginError(e)
            } finally {
                _isLoading.value = false // Stop loading
            }
        }
    }
    private fun handleLoginError(e: Exception) {
        val errorMessage = when (e) {
            is HttpException -> {
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    val json = JSONObject(errorBody.toString())
                    json.getString("message")
                } catch (jsonException: JSONException) {
                    "Login failed. Please try again."
                }
            }
            else -> "Login failed. Please try again."
        }
        _loginResult.value = Result.Failure(Throwable(errorMessage))
        Log.e(TAG, "Login failed", e)
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}