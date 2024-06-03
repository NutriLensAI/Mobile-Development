package com.capstone.mobiledevelopment.nutrilens.view.hasil

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.adapter.Makanan
import com.capstone.mobiledevelopment.nutrilens.view.adapter.MakananAdapter

class Ingredients : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        val imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) }
        val makananList: List<Makanan> = intent.getParcelableArrayListExtra("makanan_list") ?: emptyList()

        val imageView: ImageView = findViewById(R.id.img_makanan)
        imageUri?.let {
            Glide.with(this)
                .load(it)
                .into(imageView)
        }

        val recyclerView: RecyclerView = findViewById(R.id.rv_ingredients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MakananAdapter(makananList)
    }
}