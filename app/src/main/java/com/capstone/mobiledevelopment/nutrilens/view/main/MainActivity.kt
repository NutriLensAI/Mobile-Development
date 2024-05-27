package com.capstone.mobiledevelopment.nutrilens.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityMainBinding
import com.capstone.mobiledevelopment.nutrilens.view.addstory.AddStoryActivity
//import com.capstone.mobiledevelopment.nutrilens.view.adapter.StoryAdapter
import com.capstone.mobiledevelopment.nutrilens.view.menu.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.menu.PilihanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    //private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.token.observe(this) { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.getStories()
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
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

                R.id.navigation_add -> {
                    val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_add)
                    startActivity(intent)
                    true
                }

                R.id.navigation_stats -> {
                    // Activity ini sudah halaman statistik
                    true
                }

                else -> false
            }
        }


        viewModel.getStories()
        observeSession()
        setupView()
       // setupRecyclerView()
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.fetchToken() // Fetch token when session changes
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    //private fun setupRecyclerView() {
        //adapter = StoryAdapter()
   // }

}
