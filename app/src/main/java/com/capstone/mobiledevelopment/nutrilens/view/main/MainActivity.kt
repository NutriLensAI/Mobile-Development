package com.capstone.mobiledevelopment.nutrilens.view.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityMainBinding
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.DrinkDatabase
import com.capstone.mobiledevelopment.nutrilens.data.database.sleep.SleepDatabase
import com.capstone.mobiledevelopment.nutrilens.view.drink.ResetDrinkWorker
import com.capstone.mobiledevelopment.nutrilens.view.resep.Resep
import com.capstone.mobiledevelopment.nutrilens.view.adapter.info.MenuAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.info.MenuItem
import com.capstone.mobiledevelopment.nutrilens.view.camera.CameraFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.StepCountWorker
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
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
import java.util.concurrent.TimeUnit
import java.util.Calendar

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
        getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
    }

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
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        initialSensorSteps = sharedPreferences.getInt("initialSensorSteps", -1)
        lastSavedSteps = sharedPreferences.getInt("lastSavedSteps", 0)

        // Fetch drink data and setup RecyclerView
        fetchDrinkDataAndSetupRecyclerView()

        // Schedule the worker to reset drink data at midnight
        scheduleResetDrinkWorker()


    }

    private fun setupViewModelObservers() {
        viewModel.stepCounts.observe(this) { stepCounts ->
            val totalSteps = stepCounts.sumOf { it.stepCount }
            fetchDrinkDataAndSetupRecyclerView(totalSteps)
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

    private fun setupPeriodicWork() {
        val workRequest = PeriodicWorkRequestBuilder<StepCountWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("StepCountWork", ExistingPeriodicWorkPolicy.REPLACE, workRequest)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this, LocalRecordingClient.LOCAL_RECORDING_CLIENT_MIN_VERSION_CODE)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, REQUEST_CODE_UPDATE_PLAY_SERVICES)?.show()
            } else {
                Toast.makeText(this, "This device is not supported.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            subscribeToFitnessData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun subscribeToFitnessData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
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

    private fun setupRecyclerView(currentSteps: Int, totalDrinkAmount: Int, sleepCount: Int) {
        val menuList = mutableListOf(
            MenuItem("Sugar", R.drawable.ic_sugar, "25 gr", "How much sugar per day?"),
            MenuItem("Sleep", R.drawable.ic_cholesterol, "$sleepCount sessions", "Number of sleep sessions"),
            MenuItem("Steps", R.drawable.ic_steps, "$currentSteps/10,000 steps", "How much should you walk every day?"),
            MenuItem("Drink", R.drawable.ic_drink, "$totalDrinkAmount ml", "How much should you drink every day?")
        )

        val adapter = MenuAdapter(menuList)
        binding.menuRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.menuRecyclerView.adapter = adapter
    }

    private fun fetchDrinkDataAndSetupRecyclerView(currentSteps: Int = 0) {
        val drinkDao = DrinkDatabase.getDatabase(this).drinkDao()
        val sleepDataDao = SleepDatabase.getDatabase(this).sleepDataDao() // Ensure this references the correct database
        lifecycleScope.launch(Dispatchers.IO) {
            val totalAmount = drinkDao.getTotalAmount() ?: 0
            val sleepCount = sleepDataDao.getAllSleepData().size
            withContext(Dispatchers.Main) {
                setupRecyclerView(currentSteps, totalAmount, sleepCount)
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
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyWorkRequest
        )
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
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

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.fetchToken()
            }
        }
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu)

        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_stats)
        bottomNavigationView.selectedItemId = selectedItemId

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_food -> {
                    val intent = Intent(this@MainActivity, Resep::class.java)
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
            val intent = Intent(this@MainActivity, CameraFoodActivity::class.java)
            startActivity(intent)
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
