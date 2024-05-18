package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.adapter.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.view.add_story.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.settings.SettingsActivity
import com.dicoding.picodiploma.loginwithanimation.view.utils.Result
import com.dicoding.picodiploma.loginwithanimation.view.utils.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvList.setHasFixedSize(true)
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.Settingsbutton.setOnClickListener {
            navigateToSettingsActivity()
        }

        val fab: FloatingActionButton = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            navigateToAddStoryActivity()
        }

        viewModel.token.observe(this) { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.getStories()
            }
        }

        viewModel.getStories()
        observeSession()
        setupView()
        setupRecyclerView()
        observeViewModel()
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
    private fun setupRecyclerView() {
        adapter = StoryAdapter()
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar3.visibility = View.VISIBLE // Show progress bar
            } else {
                binding.progressBar3.visibility = View.GONE // Hide progress bar
            }
        }

        // Observe stories result
        viewModel.storiesResult.observe(this) { result ->
            if (result is Result.Success) {
                val stories = result.value.listStory
                adapter.submitList(stories)
            } else if (result is Result.Failure) {
                val errorMessage = result.error.message
                if (errorMessage != null) {
                    showErrorMessage(errorMessage)
                }
                // Ensure loading state is also updated when there's a failure
                hideLoading()
            }
        }
    }

    private fun hideLoading() {
        binding.progressBar3.visibility = View.GONE
    }

    private fun showErrorMessage(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAddStoryActivity() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
    }
}