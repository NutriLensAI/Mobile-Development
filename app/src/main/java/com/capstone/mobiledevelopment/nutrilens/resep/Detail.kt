package com.capstone.mobiledevelopment.nutrilens.resep

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityDetailBinding

class Detail : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageRes = intent.getStringExtra("EXTRA_IMAGE")?.let { getImageResource(it) }
        val title = intent.getStringExtra("EXTRA_TITLE")
        val description = intent.getStringExtra("EXTRA_DESCRIPTION")

        imageRes?.let { binding.ivDetailImage.setImageResource(it) }
        binding.tvDetailTitle.text = title
        binding.tvDetailDescription.text = description
    }

    private fun getImageResource(imageName: String): Int {
        return resources.getIdentifier(imageName, "drawable", packageName)
    }
}


