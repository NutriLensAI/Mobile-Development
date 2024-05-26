package com.capstone.mobiledevelopment.nutrilens.view.add_story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityAddStoryBinding
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.utils.getImageUri
import com.capstone.mobiledevelopment.nutrilens.view.utils.reduceFileImage
import com.capstone.mobiledevelopment.nutrilens.view.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private var currentImageUri: Uri? = null
    private lateinit var binding: ActivityAddStoryBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            binding.cameraButton.isEnabled = true
        }
        }

    override fun onResume() {
        super.onResume()
        showImage()
    }

    // Handle the result of the camera permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission granted
                    binding.cameraButton.isEnabled = true
                } else {
                    // Camera permission denied
                    Toast.makeText(
                        this,
                        "Camera permission is required to proceed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
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
            val token = "Bearer ${viewModel.getToken()}"
            val imageFile = uriToFile(uri, this@AddStoryActivity).reduceFileImage()
            val descriptionEditText = findViewById<EditText>(R.id.ed_add_description)
            val description = descriptionEditText.text.toString()

            // Check if location switch is enabled
            if (binding.locationSwitch.isChecked) {
                // Location switch is ON, get current location
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@AddStoryActivity)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Request location permissions if not granted
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                    return
                }
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        val lat = location?.latitude?.toFloat() ?: 0.0f
                        val lon = location?.longitude?.toFloat() ?: 0.0f
                        proceedWithImageUpload(token, imageFile, description, lat, lon)
                    }
                    .addOnFailureListener { e ->
                        // Handle failure to get location
                        showToast(getString(R.string.location_error) + " " + e.message)
                        proceedWithImageUpload(token, imageFile, description, 0.0f, 0.0f)
                    }
            } else {
                // Location switch is OFF, use default values
                proceedWithImageUpload(token, imageFile, description, 0.0f, 0.0f)
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun proceedWithImageUpload(token: String, imageFile: File, description: String, lat: Float, lon: Float) {
        viewModel.uploadImage(token, imageFile, description, lat, lon)
        observeUploadResult()
    }

    private fun observeUploadResult() {
        viewModel.uploadResult.observe(this) { result ->
            val successMessage = getString(R.string.upload_success)
            val failureMessage = getString(R.string.upload_failure)
            if (result is Result.Success) {
                showToast(successMessage)
                showLoading(false)
                navigateToMainActivity()
            } else if (result is Result.Failure) {
                val errorMessage = "$failureMessage: ${result.error.message}"
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 102
    }
}
