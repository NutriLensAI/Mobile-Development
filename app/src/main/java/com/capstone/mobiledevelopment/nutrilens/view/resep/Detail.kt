package com.capstone.mobiledevelopment.nutrilens.view.resep

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityDetailBinding

class Detail : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("EXTRA_TITLE")
        val ingredients = intent.getStringExtra("EXTRA_INGREDIENTS")
        val steps = intent.getStringExtra("EXTRA_STEPS")

        binding.tvDetailTitle.text = title
        binding.tvDetailIngredients.text = ingredients?.replace("--", "\n")
        binding.tvDetailSteps.text = steps?.replace("--", "\n")
    }
}


