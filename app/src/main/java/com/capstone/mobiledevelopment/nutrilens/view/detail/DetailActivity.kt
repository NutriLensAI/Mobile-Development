package com.capstone.mobiledevelopment.nutrilens.view.detail

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.reponse.ListStoryItem

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_detail)

            val story: ListStoryItem? = intent.getParcelableExtra("story")

            // Access the views in your layout
            val judulTextView: TextView = findViewById(R.id.tv_detail_name)
            val deskripsiTextView: TextView = findViewById(R.id.tv_detail_description)
            val imageView: ImageView = findViewById(R.id.iv_detail_photo)

            // Check if the story object is not null
            if (story != null) {
                // Set the text of judulTextView to the name of the story
                judulTextView.text = story.name

                // Set the text of deskripsiTextView to the description of the story
                deskripsiTextView.text = story.description

                // Load the image using Glide or any other image loading library
                Glide.with(this)
                    .load(story.photoUrl)
                    .into(imageView)
            }
        }
}