package com.dicoding.picodiploma.loginwithanimation.view.add_story

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.utils.Result
import com.dicoding.picodiploma.loginwithanimation.view.utils.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.utils.getImageUri
import com.dicoding.picodiploma.loginwithanimation.view.utils.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.view.utils.uriToFile
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {
    private var currentImageUri: Uri? = null
    private lateinit var currentImageFile: File
    private lateinit var binding: ActivityAddStoryBinding
    private val CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE = 102
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            binding.cameraButton.isEnabled = true
        }
    }
    // Handle the result of the camera permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE -> {
                // Check if all requested permissions are granted
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Both permissions granted, enable camera button
                    binding.cameraButton.isEnabled = true
                } else {
                    // Permissions denied, show a message or handle accordingly
                    Toast.makeText(
                        this,
                        "Camera and storage permissions are required to proceed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startGallery() {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherUcrop.launch(pickImageIntent)
    }

    private val launcherUcrop = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let { uri ->
                currentImageFile = createImageFile()
                val destinationUri = Uri.fromFile(currentImageFile)

                val maxWidth = 800 // Example max width
                val maxHeight = 600 // Example max height

                // Define custom aspect ratio options
                val aspectRatioOptions = listOf(
                    AspectRatio("16:9", 16f, 9f),
                    AspectRatio("4:3", 4f, 3f),
                    AspectRatio("1:1", 1f, 1f)
                )

                val options = UCrop.Options().apply {
                    setAspectRatioOptions(0, *aspectRatioOptions.toTypedArray())
                }

                UCrop.of(uri, destinationUri)
                    .withOptions(options)
                    .withMaxResultSize(maxWidth, maxHeight)
                    .start(this@AddStoryActivity)
            }
        } else {
            showToast("No image selected")
        }
    }

    private fun createImageFile(): File {
        // Create an image file name with a unique timestamp
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "cropped_image_$timeStamp",  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                currentImageUri = it // Update currentImageUri with the URI of the cropped image
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.message?.let {
                showToast(it)
            }
        }
    }

    private fun showImage() {
        // TODO: Display the image corresponding to the currentImageUri.
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivStoryImage.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            lifecycleScope.launch {
                val token = "Bearer ${viewModel.getToken()}"
                val imageFile = uriToFile(uri, this@AddStoryActivity).reduceFileImage()
                val descriptionEditText = findViewById<EditText>(R.id.ed_add_description)
                val description = descriptionEditText.text.toString()
                showLoading(true)
                try {
                    viewModel.uploadImage(token, imageFile, description)
                } catch (e: Exception) {
                    showToast(getString(R.string.upload_failure) + " " + e.message)
                    showLoading(false)
                    return@launch
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))

        // Observe the upload result
        viewModel.uploadResult.observe(this) { result ->
            val successMessage = getString(R.string.upload_success)
            val failureMessage = getString(R.string.upload_failure)
            if (result is Result.Success) {
                showToast(successMessage)
                showLoading(false)
                navigateToMainActivity()
            } else if (result is Result.Failure) {
                val errorMessage = "${failureMessage}: ${result.error.message}"
                showToast(errorMessage)
                showLoading(false)
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar5.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}