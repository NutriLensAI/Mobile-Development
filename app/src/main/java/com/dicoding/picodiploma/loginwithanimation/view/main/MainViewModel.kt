package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.reponse.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.reponse.StoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    val storiesPagingData: LiveData<PagingData<ListStoryItem>> = _token.switchMap { token ->
        storyRepository.getStories(token).cachedIn(viewModelScope)
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    // Fetch token
    fun fetchToken() {
        viewModelScope.launch {
            val userModel = userRepository.getSession().first()
            _token.value = userModel.token
        }
    }

    // Manually trigger fetching stories
    fun getStories(): LiveData<StoriesResponse> {
        val token = _token.value ?: return MutableLiveData<StoriesResponse>().apply {
            value = null
        }
        val liveData = MutableLiveData<StoriesResponse>()
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = storyRepository.getStoriesResponse(token)
                liveData.postValue(response)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                Log.e(TAG, "Failed to fetch stories", e)
            }
        }
        return liveData
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}