package com.capstone.mobiledevelopment.nutrilens.view.resep

import android.os.Bundle
import android.view.View
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.step.AppDatabase
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityDetailBinding
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Detail : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: AppDatabase
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "nutrilens-db"
        ).build()

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
            isFavorite = !isFavorite
            updateFavoriteButton()
            handleFavoriteClick(title!!, ingredients!!, steps!!)
        }
    }

    private fun updateFavoriteButton() {
        if (isFavorite) {
            binding.ivFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart_filled))
        } else {
            binding.ivFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart_outline))
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
}
