package com.capstone.mobiledevelopment.nutrilens.view.welcome

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityWelcomeBinding
import com.capstone.mobiledevelopment.nutrilens.view.login.LoginActivity
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    override fun onResume() {
        super.onResume()
        playAnimations()
    }

    private fun playAnimations() {
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val imageView = binding.imageView
        val drawable = imageView.drawable

        val imageViewAnimationSet = AnimationSet(true)
        imageViewAnimationSet.addAnimation(zoomInAnimation)
        imageViewAnimationSet.addAnimation(fadeInAnimation)
        binding.imageView.startAnimation(imageViewAnimationSet)

        val titleTextViewAnimationSet = AnimationSet(true)
        titleTextViewAnimationSet.addAnimation(fadeInAnimation)
        binding.titleTextView.startAnimation(titleTextViewAnimationSet)

        val descTextViewAnimationSet = AnimationSet(true)
        descTextViewAnimationSet.addAnimation(fadeInAnimation)
        binding.descTextView.startAnimation(descTextViewAnimationSet)

        val loginButtonAnimationSet = AnimationSet(true)
        loginButtonAnimationSet.addAnimation(slideUpAnimation)
        binding.loginButton.startAnimation(loginButtonAnimationSet)

        val signupButtonAnimationSet = AnimationSet(true)
        signupButtonAnimationSet.addAnimation(slideUpAnimation)
        binding.signupButton.startAnimation(signupButtonAnimationSet)

        val guestButton: TextView = findViewById(R.id.guestButton)
        val guestButtonAnimationSet = AnimationSet(true)
        guestButtonAnimationSet.addAnimation(slideUpAnimation)
        guestButton.startAnimation(guestButtonAnimationSet)

        if (drawable is AnimatedVectorDrawable) {
            drawable.start()
        }
    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.isAppearanceLightStatusBars =
                true // Optional: Set status bar content to dark
        }
        supportActionBar?.hide()

        // Set status bar color to green
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupWelcome::class.java))
        }

        val guestButton: TextView = findViewById(R.id.guestButton)
        guestButton.setOnClickListener {
            loginAsGuest()
        }
    }

    private fun loginAsGuest() {
        val userPreference = UserPreference.getInstance(dataStore)
        lifecycleScope.launch {
            val guestUser = UserModel(
                email = "guest@guest.com",
                token = "guest_token",
                isLogin = true,
                username = "Guest",
                isGuest = true
            )
            userPreference.saveSession(guestUser)
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
