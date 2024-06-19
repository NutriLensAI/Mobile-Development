package com.capstone.mobiledevelopment.nutrilens.view.resep

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.favorite.FavoriteRecipe
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityDetailBinding
import com.capstone.mobiledevelopment.nutrilens.view.welcome.WelcomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: StepDatabase
    private var isFavorite: Boolean = false
    private var isGuestUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = StepDatabase.getDatabase(applicationContext)

        val userPreference = UserPreference.getInstance(dataStore)
        lifecycleScope.launch {
            val userModel = userPreference.getSession().first()
            isGuestUser = userModel.isGuest
        }

        val title = intent.getStringExtra("EXTRA_TITLE")
        val ingredients = intent.getStringExtra("EXTRA_INGREDIENTS")
        val steps = intent.getStringExtra("EXTRA_STEPS")

        binding.tvDetailTitle.text = title
        binding.tvDetailIngredients.text = ingredients?.replace("--", "\n")
        binding.tvDetailSteps.text = steps?.replace("--", "\n")

        CoroutineScope(Dispatchers.Main).launch {
            val favoriteRecipe = db.favoriteRecipeDao().getFavoriteByTitle(title!!)
            isFavorite = favoriteRecipe != null
            updateFavoriteButton()
        }

        binding.ivFavorite.setOnClickListener {
            if (isGuestUser) {
                showLoginDialog()
            } else {
                isFavorite = !isFavorite
                updateFavoriteButton()
                handleFavoriteClick(title!!, ingredients!!, steps!!)
            }
        }
        setupView()
    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
            controller.isAppearanceLightStatusBars = true // Set status bar content to dark
            controller.isAppearanceLightNavigationBars = true // Set navigation bar content to dark
        }
        supportActionBar?.hide()

        // Set status bar color to white
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.white) // Set navigation bar color to white
    }

    private fun updateFavoriteButton() {
        if (isFavorite) {
            binding.ivFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_heart_filled
                )
            )
        } else {
            binding.ivFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_heart_outline
                )
            )
        }
        animateFavoriteButton(binding.ivFavorite)
    }

    private fun animateFavoriteButton(button: View) {
        val scaleAnimation = ScaleAnimation(
            0.7f, 1.2f, 0.7f, 1.2f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 200
        scaleAnimation.fillAfter = true
        button.startAnimation(scaleAnimation)
    }

    private fun handleFavoriteClick(title: String, ingredients: String, steps: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (isFavorite) {
                db.favoriteRecipeDao().insertFavorite(
                    FavoriteRecipe(
                        title = title,
                        ingredients = ingredients,
                        steps = steps
                    )
                )
            } else {
                db.favoriteRecipeDao().removeFavoriteByTitle(title)
            }
        }
    }

    private fun showLoginDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.kamu_harus_login_untuk_menggunakan_fitur_ini))
        builder.setMessage(R.string.silakan_login_untuk_melanjutkan_atau_pilih_later_untuk_menggunakan_akun_guest)
        builder.setPositiveButton("Login Now") { dialog, _ ->
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Later") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}
