package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.reponse.StoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.repository.UserRepository
import com.dicoding.picodiploma.loginwithanimation.view.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository,
                    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storiesResult = MutableLiveData<Result<StoriesResponse>>()
    val storiesResult: LiveData<Result<StoriesResponse>> = _storiesResult

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    // Fetch token
    fun fetchToken() {
        viewModelScope.launch {
            val userModel = userRepository.getSession().first()
            _token.value = userModel.token
            getStories()
        }
    }

    fun getStories() {
        val tokenValue = _token.value
        if (!tokenValue.isNullOrEmpty()) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val response = storyRepository.getStories(tokenValue)
                    _storiesResult.value = Result.Success(response)
                } catch (e: Exception) {
                    _storiesResult.value = Result.Failure(e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}
