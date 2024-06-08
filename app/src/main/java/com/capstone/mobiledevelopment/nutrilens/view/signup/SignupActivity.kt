package com.capstone.mobiledevelopment.nutrilens.view.signup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivitySignupBinding
import com.capstone.mobiledevelopment.nutrilens.view.login.LoginActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.Result
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var passwordEditTextLayout: TextInputLayout
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        passwordEditTextLayout = findViewById(R.id.passwordEditTextLayout)
        setupView()
        setupAction()
        observeRegisterResult()
    }

    override fun onResume() {
        super.onResume()
        playAnimations()
    }

    private fun playAnimations() {
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // Do nothing
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.imageView.visibility = View.VISIBLE
                binding.titleTextView.visibility = View.VISIBLE
                binding.nameTextView.visibility = View.VISIBLE
                binding.nameEditTextLayout.visibility = View.VISIBLE
                binding.emailTextView.visibility = View.VISIBLE
                binding.emailEditTextLayout.visibility = View.VISIBLE
                binding.passwordTextView.visibility = View.VISIBLE
                binding.passwordEditTextLayout.visibility = View.VISIBLE
                binding.signupButton.visibility = View.VISIBLE
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

        val nameTextViewAnimationSet = AnimationSet(true)
        nameTextViewAnimationSet.addAnimation(fadeInAnimation)
        binding.nameTextView.startAnimation(nameTextViewAnimationSet)
        binding.nameEditTextLayout.startAnimation(nameTextViewAnimationSet)

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
        binding.signupButton.startAnimation(signupButtonAnimationSet)
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
        binding.signupButton.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString()
            val name = binding.edRegisterName.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            // Register the user
            viewModel.register(name, email, password)

            // Show progress bar
            binding.progressBar2.visibility = View.VISIBLE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar2.visibility = View.VISIBLE // Show progress bar
            } else {
                binding.progressBar2.visibility = View.GONE // Hide progress bar
            }
        }
    }

    private fun observeRegisterResult() {
        viewModel.registrationResult.observe(this) { result ->
            if (result is Result.Success) {
                // Registration succeeded, show success dialog
                showSuccessDialog(binding.edRegisterEmail.text.toString())
            } else {
                val message = when (result) {
                    is Result.Failure -> getString(R.string.registration_failed) + " [" + result.error.message + "]. " + getString(R.string.try_again)
                    else -> getString(R.string.registration_failed) + ". " + getString(R.string.try_again)
                }
                showFailureDialog(message)
            }
        }
    }

    private fun showSuccessDialog(email: String) {
        val message = getString(R.string.success_dialog_message, email)
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success_dialog_title))
            setMessage(message)
            setPositiveButton(getString(R.string.success_dialog_button)) { _, _ ->
                // Move to LoginActivity
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showFailureDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.failure_dialog_title))
            setMessage(message)
            setPositiveButton(getString(R.string.failure_dialog_button)) { _, _ ->
            }
            create()
            show()
        }
    }
}
