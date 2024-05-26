package com.dicoding.picodiploma.loginwithanimation.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.utils.Result
import com.dicoding.picodiploma.loginwithanimation.view.utils.ViewModelFactory
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
        passwordValidation()
    }

    override fun onResume() {
        super.onResume()
        playAnimations()
    }

    private fun playAnimations() {
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val zoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down)

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
        signupButtonAnimationSet.addAnimation(slideUpAnimation)
        binding.loginButton.startAnimation(signupButtonAnimationSet)
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
    }

    private fun passwordValidation() {
        passwordEditTextLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM // Set end icon mode to custom

        passwordEditTextLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Check if the password is less than 8 characters
                if (s.toString().length < 8) {
                    // Set the custom error indicator
                    val errorDrawable = ContextCompat.getDrawable(this@LoginActivity, R.drawable.error_indicator)
                    passwordEditTextLayout.error = getString(R.string.password_error)
                    passwordEditTextLayout.isErrorEnabled = true
                    passwordEditTextLayout.errorIconDrawable = errorDrawable

                } else {
                    // Clear the error message and error indicator
                    passwordEditTextLayout.error = null
                    passwordEditTextLayout.isErrorEnabled = false

                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
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

                val user = UserModel(email, token)

                // Save session and update token
                viewModel.saveSessionAndNavigate(user, token).observe(this) { isSaved ->
                    if (isSaved) {
                        navigateToMainActivity() // Navigate to main activity only after saving session and updating token
                    } else {
                        showFailureToast(getString(R.string.login_failed))
                    }
                }

            } else {
                // Handle login failure
                val message = when (result) {
                    is Result.Failure -> getString(R.string.login_failed) + " [" + result.error.message + "]. " + getString(R.string.try_again)
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