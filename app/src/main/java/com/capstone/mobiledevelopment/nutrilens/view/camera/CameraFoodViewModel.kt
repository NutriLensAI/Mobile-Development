package com.capstone.mobiledevelopment.nutrilens.view.add_story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mobiledevelopment.nutrilens.data.reponse.PredictImageResponse
import com.capstone.mobiledevelopment.nutrilens.data.repository.FoodRepository
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CameraFoodViewModel(private val foodRepository: FoodRepository) : ViewModel() {
    private val _predictResult = MutableLiveData<Result<PredictImageResponse>>()
    val predictResult: LiveData<Result<PredictImageResponse>> = _predictResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun predictImage(imageFile: File) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "file",
                    imageFile.name,
                    requestImageFile
                )
                val response = foodRepository.predictImage(multipartBody)
                if (response.detail != null) {
                    // Handle validation error
                    _predictResult.value =
                        Result.Failure(Throwable(response.detail.joinToString { it.msg }))
                } else {
                    _predictResult.value = Result.Success(response)
                }
            } catch (e: Exception) {
                handlePredictError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handlePredictError(e: Exception) {
        Log.e("CameraFoodViewModel", "Prediction failed: ${e.message}", e)
        _predictResult.value = Result.Failure(e)
    }
}