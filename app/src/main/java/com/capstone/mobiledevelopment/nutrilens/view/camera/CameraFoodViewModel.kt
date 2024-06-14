package com.capstone.mobiledevelopment.nutrilens.view.add_story

//class CameraFoodViewModel(private val userRepository: UserRepository,
//                          private val foodRepository: FoodRepository) : ViewModel() {
//
//    private val _uploadResult = MutableLiveData<Result<StoriesResponse>>()
//    val uploadResult: LiveData<Result<StoriesResponse>> = _uploadResult
//
//    private val _token = MutableLiveData<String>()
//    val token: LiveData<String> = _token
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = _isLoading
//
//    fun getToken() {
//        viewModelScope.launch {
//            val userModel = userRepository.getSession().first()
//            _token.value = userModel.token
//        }
//    }
//
//    fun uploadImage( token: String ,imageFile: File, description: String, lat: Float, lon: Float) {
//        viewModelScope.launch {
//            try {
//                _isLoading.value = true
//                val requestBody = description.toRequestBody("text/plain".toMediaType())
//                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
//                val multipartBody = MultipartBody.Part.createFormData(
//                    "photo",
//                    imageFile.name,
//                    requestImageFile
//                )
//                val successResponse = foodRepository.uploadImage(token,multipartBody, requestBody)
//                _uploadResult.value = Result.Success(successResponse)
//            } catch (e: Exception) {
//                handleUploadError(e)
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    private fun handleUploadError(e: Exception) {
//        val errorMessage = when (e) {
//            is HttpException -> {
//                try {
//                    val errorBody = e.response()?.errorBody()?.string()
//                    val errorResponse = Gson().fromJson(errorBody, StoriesResponse::class.java)
//                    errorResponse.message
//                } catch (jsonException: JsonSyntaxException) {
//                    "Upload failed. Please try again."
//                } catch (ioException: IOException) {
//                    "Network error. Please check your internet connection."
//                }
//            }
//            else -> "Upload failed. Please try again."
//        }
//        _uploadResult.value = Result.Failure(Throwable(errorMessage))
//    }
//}