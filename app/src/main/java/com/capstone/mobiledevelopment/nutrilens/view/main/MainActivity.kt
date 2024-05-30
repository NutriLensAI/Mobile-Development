package com.capstone.mobiledevelopment.nutrilens.view.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.capstone.mobiledevelopment.nutrilens.view.adapter.MenuAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.MenuItem
import com.capstone.mobiledevelopment.nutrilens.view.addfood.AddFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.pilihan.PilihanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.utils.StepCountWorker
import com.capstone.mobiledevelopment.nutrilens.view.utils.step.StepCounter
import com.capstone.mobiledevelopment.nutrilens.view.welcome.WelcomeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private var totalSteps = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            initializeStepCounter()
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

        // Load today's steps and set up the RecyclerView initially
        viewModel.loadTodaySteps().observe(this) { steps ->
            totalSteps = steps.toInt()
            setupRecyclerView(totalSteps)
        }

        // Schedule the worker to run periodically
        setupPeriodicWork()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initializeStepCounter() {
        // Log the initialization process
        Log.d("MainActivity", "Initializing SensorManager")
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Initialize StepCounter
        val stepCounter = StepCounter(sensorManager, viewModel.userRepository)
        stepCounter.stepFlow().onEach { steps ->
            // Handle step count updates
            totalSteps = steps.toInt()
            setupRecyclerView(totalSteps)
        }.launchIn(lifecycleScope)

        viewModel.startStepCounter()
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
                initializeStepCounter()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPeriodicWork() {
        val workRequest = PeriodicWorkRequestBuilder<StepCountWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "StepCountWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
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

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10

        @RequiresApi(Build.VERSION_CODES.Q)
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
        private const val TAG = "MainActivity"
    }
}