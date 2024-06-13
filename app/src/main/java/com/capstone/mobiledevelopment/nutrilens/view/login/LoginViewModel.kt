package com.capstone.mobiledevelopment.nutrilens.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.reponse.LoginResponse
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Macros
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _sessionSaved = MutableLiveData<Boolean>()
    val sessionSaved: LiveData<Boolean> = _sessionSaved

    private val _userProfile = MutableLiveData<RegisterResponse>()
    val userProfile: LiveData<RegisterResponse> = _userProfile

    private val _macros = MutableLiveData<Macros?>()
    val macros: MutableLiveData<Macros?> = _macros

    fun login(email: String, password: String) {
        _isLoading.value = true // Start loading
        viewModelScope.launch {
            try {
                Log.d(TAG, "Login started")
                val response = userRepository.login(email, password)
                _loginResult.value = Result.Success(response)
                Log.d(TAG, "Login successful")
            } catch (e: Exception) {
                handleLoginError(e)
            } finally {
                _isLoading.value = false // Stop loading
            }
        }
    }

    fun saveSession(token: String) {
        viewModelScope.launch {
            try {
                val userProfile = userRepository.getUserProfile(token)
                val user = UserModel(
                    email = userProfile.email.toString(),
                    token = token,
                    isLogin = true,
                    username = userProfile.username.toString()
                )
                userRepository.saveSession(user)
                _userProfile.value = userProfile
                _sessionSaved.value = true
                Log.d(TAG, "User details fetched and session saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch user details and save session", e)
                _sessionSaved.value = false
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