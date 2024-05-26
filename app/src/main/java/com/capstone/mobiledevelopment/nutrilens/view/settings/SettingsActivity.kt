package com.capstone.mobiledevelopment.nutrilens.view.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivitySettingsBinding
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.welcome.WelcomeActivity

class SettingsActivity : AppCompatActivity() {
    private val viewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.fetchEmail()
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
        observeEmail()
        setupView()
        setupAction()
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

    private fun observeEmail() {
        viewModel.userEmail.observe(this) { userEmail ->
            val greetingMessage = getString(R.string.greeting, userEmail)
            binding.nameTextView.text = greetingMessage
        }
    }

    private fun setupAction() {
        binding.actionLogout.setOnClickListener {
            showLogoutConfirmationDialog()}

        binding.translateButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.logout))
            setMessage(getString(R.string.logout_message))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                navigateToWelcomeActivity()
                viewModel.logout()
            }
            create()
            show()
        }
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}