package com.capstone.mobiledevelopment.nutrilens.view.camera

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityAddFoodBinding
import com.capstone.mobiledevelopment.nutrilens.view.add_story.CameraFoodViewModel
import com.capstone.mobiledevelopment.nutrilens.view.hasil.HasilMakananActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.utils.getImageUri
import com.capstone.mobiledevelopment.nutrilens.view.utils.reduceFileImage
import com.capstone.mobiledevelopment.nutrilens.view.utils.uriToFile
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class CameraFoodActivity : AppCompatActivity() {
    private val viewModel by viewModels<CameraFoodViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddFoodBinding
    private var imageCapture: ImageCapture? = null
    private var flashEnabled = false
    private lateinit var outputDirectory: File
    private lateinit var capturedImageUri: Uri

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                startCrop(it)
            }
        }

    private val cropImage = registerForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                saveCroppedImageToGallery(uri)
            }
        } else {
            val exception = result.error
            Log.e(TAG, "Crop failed: ${exception?.message}", exception)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        outputDirectory = getOutputDirectory()

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        val closeButton: ImageButton = findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            finish()
        }

        val flashButton: ImageButton = findViewById(R.id.flashButton)
        flashButton.setOnClickListener {
            flashEnabled = !flashEnabled
            updateFlashButtonIcon(flashButton)
            startCamera()
        }

        val captureButton: ImageButton = findViewById(R.id.captureButton)
        captureButton.setOnClickListener {
            takePhoto()
        }

        val galleryButton: ImageButton = findViewById(R.id.galleryButton)
        galleryButton.setOnClickListener {
            openGallery()
        }

        val takePictureText: TextView = findViewById(R.id.takePictureText)
        takePictureText.setOnClickListener {
            takePhoto()
        }

        viewModel.predictResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val prediction = result.value.prediction
                    val confidence = result.value.confidence
                    // Pass the prediction result along with the image URI
                    navigateToHasilMakanan(capturedImageUri, prediction, confidence)
                }
                is Result.Failure -> {
                    val error = result.error.message
                    showErrorDialog(error)
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Show or hide loading indicator
        }
    }

    private fun updateFlashButtonIcon(flashButton: ImageButton) {
        val icon = if (flashEnabled) R.drawable.ic_flash_on else R.drawable.ic_flash_off
        flashButton.setImageResource(icon)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setFlashMode(if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(this.windowManager.defaultDisplay.rotation)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        capturedImageUri = getImageUri(this)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    SimpleDateFormat(
                        FILENAME_FORMAT,
                        Locale.US
                    ).format(System.currentTimeMillis()) + ".jpg"
                )
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
                }
            }
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(
                        baseContext,
                        "Photo capture failed: ${exc.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: capturedImageUri
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    // Start cropping the captured image
                    startCrop(savedUri)
                }
            }
        )
    }

    private fun startCrop(uri: Uri) {
        val cropImageContractOptions = CropImageContractOptions(
            uri,
            CropImageOptions(
                imageSourceIncludeGallery = true,
                imageSourceIncludeCamera = true
            )
        )
        cropImage.launch(cropImageContractOptions)
    }

    private fun saveCroppedImageToGallery(croppedUri: Uri) {
        val file = uriToFile(croppedUri, this)
        val reducedFile = file.reduceFileImage()
        val finalUri = savePhotoToGallery(reducedFile)
        capturedImageUri = finalUri
        // Call predictImage from the ViewModel
        viewModel.predictImage(reducedFile)
    }

    private fun savePhotoToGallery(reducedFile: File): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, reducedFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "DCIM/${resources.getString(R.string.app_name)}"
                )
            }
        }

        val resolver = applicationContext.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                reducedFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream!!)
                }
            }
        }

        return uri ?: Uri.fromFile(reducedFile)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun navigateToHasilMakanan(imageUri: Uri, prediction: String?, confidence: Double?) {
        val intent = Intent(this, HasilMakananActivity::class.java).apply {
            putExtra("image_uri", imageUri.toString())
            putExtra("prediction", prediction)
            putExtra("confidence", confidence)
        }
        startActivity(intent)
    }

    private fun showErrorDialog(error: String?) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(error)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        private const val TAG = "AddFoodActivity"
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}