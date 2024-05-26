package com.capstone.mobiledevelopment.nutrilens.view.add_story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.reponse.StoriesResponse
import com.capstone.mobiledevelopment.nutrilens.data.repository.StoryRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class AddStoryViewModel(private val userRepository: UserRepository,
                        private val storyRepository: StoryRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<StoriesResponse>>()
    val uploadResult: LiveData<Result<StoriesResponse>> = _uploadResult

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getToken() {
        viewModelScope.launch {
            val userModel = userRepository.getSession().first()
            _token.value = userModel.token
        }
    }

    fun uploadImage( token: String ,imageFile: File, description: String, lat: Float, lon: Float) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                val successResponse = storyRepository.uploadImage(token,multipartBody, requestBody, lat, lon)
                _uploadResult.value = Result.Success(successResponse)
            } catch (e: Exception) {
                handleUploadError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleUploadError(e: Exception) {
        val errorMessage = when (e) {
            is HttpException -> {
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, StoriesResponse::class.java)
                    errorResponse.message
                } catch (jsonException: JsonSyntaxException) {
                    "Upload failed. Please try again."
                } catch (ioException: IOException) {
                    "Network error. Please check your internet connection."
                }
            }
            else -> "Upload failed. Please try again."
        }
        _uploadResult.value = Result.Failure(Throwable(errorMessage))
    }
}