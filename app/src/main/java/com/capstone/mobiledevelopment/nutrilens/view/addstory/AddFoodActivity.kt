package com.capstone.mobiledevelopment.nutrilens.view.addstory

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
import com.capstone.mobiledevelopment.nutrilens.view.add_story.AddFoodViewModel
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.menu.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.menu.PilihanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.utils.getImageUri
import com.capstone.mobiledevelopment.nutrilens.view.utils.reduceFileImage
import com.capstone.mobiledevelopment.nutrilens.view.utils.uriToFile
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class AddFoodActivity : AppCompatActivity() {
    private var currentImageUri: Uri? = null
    private lateinit var binding: ActivityAddStoryBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private val viewModel by viewModels<AddFoodViewModel> {
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

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_stats)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_food -> {
                    val intent = Intent(this@AddFoodActivity, PilihanMakanan::class.java)
                    intent.putExtra("selected_item", R.id.navigation_food)
                    startActivity(intent)
                    true
                }

                R.id.navigation_profile -> {
                    val intent = Intent(this@AddFoodActivity, SettingsActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_profile)
                    startActivity(intent)
                    true
                }

                R.id.navigation_stats -> {
                    val intent = Intent(this@AddFoodActivity, MainActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_stats)
                    startActivity(intent)
                    true
                }

                R.id.navigation_documents -> {
                    val intent = Intent(this@AddFoodActivity, CatatanMakanan::class.java)
                    intent.putExtra("selected_item", R.id.navigation_documents)
                    startActivity(intent)
                    true
                }
                R.id.navigation_add -> {
                    true
                }
                else -> false
            }
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
            val imageFile = uriToFile(uri, this@AddFoodActivity).reduceFileImage()
            val descriptionEditText = findViewById<EditText>(R.id.ed_add_description)
            val description = descriptionEditText.text.toString()
                proceedWithImageUpload(token, imageFile, description, 0.0f, 0.0f)
            }
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
}
