package com.capstone.mobiledevelopment.nutrilens.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.DrinkDatabase
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Macros
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityMainBinding
import com.capstone.mobiledevelopment.nutrilens.view.adapter.info.MenuAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.info.MenuItem
import com.capstone.mobiledevelopment.nutrilens.view.camera.CameraFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.login.LoginActivity
import com.capstone.mobiledevelopment.nutrilens.view.resep.ResepActivity
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.Utils
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.utils.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.utils.worker.ResetDrinkWorker
import com.capstone.mobiledevelopment.nutrilens.view.welcome.WelcomeActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.fitness.FitnessLocal
import com.google.android.gms.fitness.LocalRecordingClient
import com.google.android.gms.fitness.data.LocalDataType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var initialSensorSteps: Int? = null
    private var lastSavedSteps: Int = 0
    private val sharedPreferences by lazy {
        getSharedPreferences("step_prefs", MODE_PRIVATE)
    }

    private var isGuestUser: Boolean = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModelObservers()
        checkAndRequestPermissions()

        setupBottomNavigation()
        setupFab()

        observeSession()
        setupView()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        initialSensorSteps = sharedPreferences.getInt("initialSensorSteps", -1)
        lastSavedSteps = sharedPreferences.getInt("lastSavedSteps", 0)

        // Fetch drink and sugar data and setup RecyclerView
        fetchDrinkAndSugarDataAndSetupRecyclerView()

        // Schedule the worker to reset drink data at midnight
        scheduleResetDrinkWorker()

        // Fetch and bind username
        viewModel.fetchUsername()
        viewModel.username.observe(this) { username ->
            binding.helloTextView.text = "Hello, $username!"
        }

        viewModel.isGuestUser().observe(this) { isGuest ->
            isGuestUser = isGuest
            if (isGuest) {
                // Jalankan logika untuk guest user
                binding.helloTextView.text = "Hello, Guest!"
            }
        }

        fetchUserDataAndMacros()
        // Bind the user image from SharedPreferences
        bindUserImageFromPreferences()
    }

    private fun bindUserImageFromPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val imageUriString = sharedPreferences.getString("profileImageUri", null)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.userImageView.setImageURI(imageUri)
        }
    }

    private fun fetchUserDataAndMacros() {
        viewModel.getSession().observe(this) { user ->
            if (user == null || !user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                user.token.let { token ->
                    if (!isGuestUser) {
                        viewModel.fetchUserProfile(token)
                        viewModel.fetchMacros(token)
                    } else {
                        showLoginDialog()
                    }
                }
            }
        }
    }

    private fun showLoginDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.kamu_harus_login_untuk_menggunakan_fitur_lengkapnya))
        builder.setMessage(getString(R.string.silakan_login_untuk_melanjutkan_atau_pilih_later_untuk_menggunakan_akun_guest))
        builder.setPositiveButton("Login Now") { dialog, _ ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("Later") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun observeSession() {
        viewModel.userProfile.observe(this) { userProfile ->
            userProfile?.let {
                val calculatedCalories = Utils.calculateTotalCalories(it)
                checkUserProfileData(it)
                // Assuming you have a method to get the actual calories
                val totalCalories = viewModel.macros.value?.totalCalories ?: 0
                binding.totalCalories.text = "${totalCalories.toInt()}/$calculatedCalories Calories"
            }
        }

        viewModel.macros.observe(this) { macros ->
            macros?.let {
                val totalCalories = viewModel.userProfile.value?.let { profile ->
                    Utils.calculateTotalCalories(profile)
                } ?: 2400
                updateMacrosUI(it, totalCalories)
            }
        }
    }

    private fun updateMacrosUI(macros: Macros, totalCalories: Int) {
        // Calculate target grams for each macro based on total calories
        val targetProteinGrams = (totalCalories * 0.15 / 4).toInt()
        val targetCarbsGrams = (totalCalories * 0.60 / 4).toInt()
        val targetFatGrams = (totalCalories * 0.15 / 9).toInt()

        binding.carbsProgressBar.progress =
            ((macros.totalCarbs ?: 0.0) * 100 / targetCarbsGrams).toInt()
        binding.fatProgressBar.progress = ((macros.totalFat ?: 0.0) * 100 / targetFatGrams).toInt()
        binding.proteinProgressBar.progress =
            ((macros.totalProteins ?: 0.0) * 100 / targetProteinGrams).toInt()
        binding.totalCalories.text =
            "${(macros.totalCalories ?: 0.0).toInt()}/$totalCalories Calories"

        binding.carbsValueTextView.text =
            formatMacroText(macros.totalCarbs ?: 0.0, targetCarbsGrams)
        binding.fatValueTextView.text = formatMacroText(macros.totalFat ?: 0.0, targetFatGrams)
        binding.proteinValueTextView.text =
            formatMacroText(macros.totalProteins ?: 0.0, targetProteinGrams)
    }

    private fun formatMacroText(value: Double, target: Int): String {
        return "${formatDecimal(value)}\nof\n$target g"
    }

    private fun formatDecimal(value: Double): String {
        return String.format("%.2f", value)
    }

    private fun checkUserProfileData(userProfile: RegisterResponse) {
        if (userProfile.weight == null || userProfile.height == null || userProfile.age == null || userProfile.gender == null || userProfile.activityLevel == null) {
            showUserProfileTooltip()
        }
    }

    private fun showUserProfileTooltip() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.complete_your_profile))
        builder.setMessage(getString(R.string.personal_data_is_still_empty_you_must_fill_it_to_have_the_fullest_experience))
        builder.setPositiveButton(getString(R.string.fill_now)) { dialog, _ ->
            // Navigate to settings activity with PersonalFragment
            val intent = Intent(this, SettingsActivity::class.java).apply {
                putExtra("selected_item", R.id.navigation_profile)
                putExtra("navigate_to", "PersonalFragment")
            }
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.later)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun setupViewModelObservers() {
        viewModel.stepCounts.observe(this) { stepCounts ->
            val totalSteps = stepCounts.sumOf { it.stepCount }
            fetchDrinkAndSugarDataAndSetupRecyclerView(totalSteps)
        }
        viewModel.token.observe(this) { token ->
            if (!token.isNullOrEmpty()) {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkAndRequestPermissions() {
        if (allPermissionsGranted()) {
            checkGooglePlayServices()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(
            this,
            LocalRecordingClient.LOCAL_RECORDING_CLIENT_MIN_VERSION_CODE
        )
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, REQUEST_CODE_UPDATE_PLAY_SERVICES)
                    ?.show()
            } else {
                Toast.makeText(this,
                    getString(R.string.this_device_is_not_supported), Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            subscribeToFitnessData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun subscribeToFitnessData() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            return
        }

        val localRecordingClient = FitnessLocal.getLocalRecordingClient(this)
        localRecordingClient.subscribe(LocalDataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem subscribing.", e)
            }
    }

    private fun setupRecyclerView(
        currentSteps: Int,
        totalDrinkAmount: Int,
        totalSugarAmount: Int,
        sleepCount: Int
    ) {
        val menuList = mutableListOf(
            MenuItem(
                "Drink",
                R.drawable.ic_drink,
                "$totalDrinkAmount ml",
                getString(R.string.how_much_should_you_drink_every_day)
            ),
            MenuItem(
                "Sugar",
                R.drawable.ic_sugar,
                "$totalSugarAmount g",
                getString(R.string.how_much_sugar_per_day)
            ),
            MenuItem(
                "Steps",
                R.drawable.ic_steps,
                "$currentSteps",
                getString(R.string.how_much_should_you_walk_every_day)
            ),
            MenuItem(
                "Sleep",
                R.drawable.ic_sleep,
                "$sleepCount sessions",
                getString(R.string.number_of_sleep_sessions)
            )
        )

        val adapter = MenuAdapter(menuList)
        binding.menuRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.menuRecyclerView.adapter = adapter
    }

    private fun fetchDrinkAndSugarDataAndSetupRecyclerView(currentSteps: Int = 0) {
        val drinkDao = DrinkDatabase.getDatabase(this).drinkDao()
        val sleepDataDao = SleepDatabase.getDatabase(this).sleepDataDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val totalAmount = drinkDao.getTotalAmount() ?: 0
            val totalSugarAmount = drinkDao.getTotalSugarAmount() ?: 0
            val sleepCount =
                sleepDataDao.getTotalSleepCount() // Use the new method to get the total sleep count
            withContext(Dispatchers.Main) {
                setupRecyclerView(currentSteps, totalAmount, totalSugarAmount, sleepCount)
            }
        }
    }

    private fun scheduleResetDrinkWorker() {
        val currentTime = Calendar.getInstance()
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val initialDelay = dueTime.timeInMillis - currentTime.timeInMillis
        val dailyWorkRequest = PeriodicWorkRequestBuilder<ResetDrinkWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ResetDrinkWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_stats)
        bottomNavigationView.selectedItemId = selectedItemId

        // Fetch drink and sugar data and setup RecyclerView
        fetchDrinkAndSugarDataAndSetupRecyclerView()

        // Register the sensor listener
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)

        // Fetch user data and macros
        viewModel.getSession().observe(this) { user ->
            user?.token?.let { token ->
                if (!isGuestUser) {
                    viewModel.fetchUserProfile(token)
                    viewModel.fetchMacros(token)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.please_login_to_use_this_macros_feature),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, stepCounterSensor)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsFromSensor = event.values[0].toInt()
            if (initialSensorSteps == null || initialSensorSteps == -1) {
                initialSensorSteps = totalStepsFromSensor
                sharedPreferences.edit().putInt("initialSensorSteps", initialSensorSteps!!).apply()
            }
            val currentSteps = totalStepsFromSensor - (initialSensorSteps ?: 0)
            if (currentSteps >= 0 && currentSteps != lastSavedSteps) {
                val stepsToSave = currentSteps - lastSavedSteps
                if (stepsToSave > 0) {
                    viewModel.saveStepCount(stepsToSave)
                    lastSavedSteps = currentSteps
                    sharedPreferences.edit().putInt("lastSavedSteps", lastSavedSteps).apply()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
            controller.isAppearanceLightStatusBars = true // Set status bar content to dark
            controller.isAppearanceLightNavigationBars = true // Set navigation bar content to dark
        }
        supportActionBar?.hide()

        // Set status bar color to white
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.white) // Set navigation bar color to white
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu)

        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_stats)
        bottomNavigationView.selectedItemId = selectedItemId

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_food -> {
                    val intent = Intent(this@MainActivity, ResepActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_food)
                    startActivity(intent)
                    true
                }

                R.id.navigation_profile -> {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_profile)
                    startActivity(intent)
                    false
                }

                R.id.navigation_documents -> {
                    val intent = Intent(this@MainActivity, CatatanMakanan::class.java)
                    intent.putExtra("selected_item", R.id.navigation_documents)
                    startActivity(intent)
                    true
                }

                R.id.navigation_stats -> true
                else -> false
            }
        }
    }

    private fun setupFab() {
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            if (isGuestUser) {
                showLoginDialog()
            } else {
                val intent = Intent(this@MainActivity, CameraFoodActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAllWork()
    }

    private fun cancelAllWork() {
        WorkManager.getInstance(this).cancelAllWorkByTag("StepCountWork")
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_UPDATE_PLAY_SERVICES = 1001

        @RequiresApi(Build.VERSION_CODES.Q)
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
        private const val TAG = "MainActivity"
    }
}