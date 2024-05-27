package com.capstone.mobiledevelopment.nutrilens.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityLoginBinding
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.signup.SignupWelcome
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var passwordEditTextLayout: TextInputLayout
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        passwordEditTextLayout = findViewById(R.id.passwordEditTextLayout)

        setupView()
        setupAction()
        observeLoginResult()
    }

    override fun onResume() {
        super.onResume()
        playAnimations()
    }

    private fun playAnimations() {
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Add AnimationListener to fadeInAnimation to keep the TextView visible after animation
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // Do nothing
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.titleTextView.visibility = View.VISIBLE
                binding.messageTextView.visibility = View.VISIBLE
                binding.emailTextView.visibility = View.VISIBLE
                binding.emailEditTextLayout.visibility = View.VISIBLE
                binding.passwordTextView.visibility = View.VISIBLE
                binding.passwordEditTextLayout.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // Do nothing
            }
        })

        val imageViewAnimationSet = AnimationSet(true)
        imageViewAnimationSet.addAnimation(zoomInAnimation)
        imageViewAnimationSet.addAnimation(fadeInAnimation)
        binding.imageView.startAnimation(imageViewAnimationSet)

        val titleTextViewAnimationSet = AnimationSet(true)
        titleTextViewAnimationSet.addAnimation(fadeInAnimation)
        binding.titleTextView.startAnimation(titleTextViewAnimationSet)

        val descTextViewAnimationSet = AnimationSet(true)
        descTextViewAnimationSet.addAnimation(fadeInAnimation)
        binding.messageTextView.startAnimation(descTextViewAnimationSet)

        val emailTextViewAnimationSet = AnimationSet(true)
        emailTextViewAnimationSet.addAnimation(fadeInAnimation)
        binding.emailTextView.startAnimation(emailTextViewAnimationSet)
        binding.emailEditTextLayout.startAnimation(emailTextViewAnimationSet)

        val passwordTextViewAnimationSet = AnimationSet(true)
        passwordTextViewAnimationSet.addAnimation(fadeInAnimation)
        binding.passwordTextView.startAnimation(passwordTextViewAnimationSet)
        binding.passwordEditTextLayout.startAnimation(passwordTextViewAnimationSet)

        val signupButtonAnimationSet = AnimationSet(true)
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        signupButtonAnimationSet.addAnimation(slideUpAnimation)
        binding.loginButton.startAnimation(signupButtonAnimationSet)
    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            // Show loading indicator when login button is clicked
            binding.progressBar.visibility = View.VISIBLE

            viewModel.login(email, password)
        }

        // Observe isLoading LiveData here
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE // Show progress bar
            } else {
                binding.progressBar.visibility = View.GONE // Hide progress bar
            }
        }

        // Add OnClickListener to signUpTextView to navigate to SignupActivity
        binding.signUpTextView.setOnClickListener {
            val intent = Intent(this, SignupWelcome::class.java)
            startActivity(intent)
        }
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(this) { result ->
            if (result is Result.Success) {
                binding.progressBar.visibility = View.GONE

                // Extract token from the API response
                val loginResponse = result.value
                val token = loginResponse.loginResult?.token ?: ""

                // Get user-provided email from the EditText
                val email = binding.edLoginEmail.text.toString()

                viewModel.saveSession(
                    UserModel(
                        email, token
                    )
                ) // Pass the UserModel and the StoryRepository instance
                viewModel.updateToken(token) // Update token in the ViewModel
                navigateToMainActivity() // Navigate to main activity

            } else {
                // Handle login failure
                val message = when (result) {
                    is Result.Failure -> getString(R.string.login_failed) + " [" + result.error.message + "]. " + getString(
                        R.string.try_again
                    )

                    else -> getString(R.string.login_failed) + ". " + getString(R.string.try_again)
                }
                showFailureToast(message)
            }
        }
    }

    private fun navigateToMainActivity() {
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showFailureToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}