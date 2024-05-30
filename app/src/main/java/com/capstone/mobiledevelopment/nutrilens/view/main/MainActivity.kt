package com.capstone.mobiledevelopment.nutrilens.view.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityMainBinding
import com.capstone.mobiledevelopment.nutrilens.view.addfood.AddFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.pilihan.PilihanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.welcome.WelcomeActivity
import com.capstone.mobiledevelopment.nutrilens.view.adapter.MenuAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.MenuItem
import com.capstone.mobiledevelopment.nutrilens.view.utils.StepCountWorker
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.fitness.FitnessLocal
import com.google.android.gms.fitness.LocalRecordingClient
import com.google.android.gms.fitness.data.LocalDataSet
import com.google.android.gms.fitness.data.LocalDataType
import com.google.android.gms.fitness.request.LocalDataReadRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private var initialSensorSteps = 0
    private var totalSteps = 0

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    private val stepCountReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val newStepCount = intent?.getIntExtra("STEP_COUNT", 0) ?: 0
            totalSteps = newStepCount
            setupRecyclerView(totalSteps)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this).registerReceiver(stepCountReceiver, IntentFilter("UPDATE_STEP_COUNT"))

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        viewModel.getStepCount().observe(this) { savedStepCount ->
            totalSteps = savedStepCount
            setupRecyclerView(totalSteps)
        }

        if (allPermissionsGranted()) {
            checkGooglePlayServices()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        setupBottomNavigation()
        setupFab()

        viewModel.token.observe(this) { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.getStories()
            }
        }

        observeSession()
        setupView()
        setupRecyclerView(totalSteps) // Initial setup with the last known steps

        setupPeriodicWork()
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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                checkGooglePlayServices()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
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
                readFitnessData()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem subscribing.", e)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readFitnessData() {
        val localRecordingClient = FitnessLocal.getLocalRecordingClient(this)
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)
        val readRequest = LocalDataReadRequest.Builder()
            .aggregate(LocalDataType.TYPE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
            .build()

        localRecordingClient.readData(readRequest).addOnSuccessListener { response ->
            Log.i(TAG, "Successfully read fitness data!")
            response.buckets.flatMap { it.dataSets }.forEach { dataSet ->
                dumpDataSet(dataSet)
            }
        }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was an error reading data", e)
            }
    }

    private fun dumpDataSet(dataSet: LocalDataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            for (field in dp.dataType.fields) {
                val stepCount = dp.getValue(field).asInt()
                Log.i(TAG, "\tLocalField: ${field.name} LocalValue: ${dp.getValue(field)}")
                updateStepCount(stepCount)
            }
        }
    }

    private fun updateStepCount(stepCount: Int) {
        totalSteps += stepCount
        viewModel.saveStepCount(totalSteps)
        setupRecyclerView(totalSteps)
    }

    private fun sendStepCountBroadcast(stepCount: Int) {
        val intent = Intent("UPDATE_STEP_COUNT")
        intent.putExtra("STEP_COUNT", stepCount)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        viewModel.saveStepCount(stepCount)
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

    private fun setupRecyclerView(currentSteps: Int) {
        val menuList = listOf(
            MenuItem("Sugar", R.drawable.ic_sugar, "25 gr", "How much sugar per day?"),
            MenuItem("Cholesterol", R.drawable.ic_cholesterol, "100 mg/dL", "Cholesterol Numbers and What They Mean"),
            MenuItem("Steps", R.drawable.ic_steps, "$currentSteps/10,000 steps", "How much should you walk every day?"),
            MenuItem("Drink", R.drawable.ic_drink, "1500 ml", "How much should you drink every day?")
        )

        val adapter = MenuAdapter(menuList)
        binding.menuRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.menuRecyclerView.adapter = adapter
        Log.d(TAG, "RecyclerView setup with current steps = $currentSteps")
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu)

        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_stats)
        bottomNavigationView.selectedItemId = selectedItemId

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_food -> {
                    val intent = Intent(this@MainActivity, PilihanMakanan::class.java)
                    intent.putExtra("selected_item", R.id.navigation_food)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_profile)
                    startActivity(intent)
                    true
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
            val intent = Intent(this@MainActivity, AddFoodActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, stepCounterSensor)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stepCountReceiver)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_UPDATE_PLAY_SERVICES = 1001
        @RequiresApi(Build.VERSION_CODES.Q)
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
        private const val TAG = "MainActivity"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsFromSensor = event.values[0].toInt()
            if (initialSensorSteps == 0) {
                initialSensorSteps = totalStepsFromSensor
            }
            val currentSteps = totalStepsFromSensor - initialSensorSteps
            totalSteps += currentSteps
            initialSensorSteps = totalStepsFromSensor
            setupRecyclerView(totalSteps)
            viewModel.saveStepCount(totalSteps)
        }
    }
}