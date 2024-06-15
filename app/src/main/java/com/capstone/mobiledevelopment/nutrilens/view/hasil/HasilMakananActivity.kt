package com.capstone.mobiledevelopment.nutrilens.view.hasil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.adapter.macros.Makanan
import com.capstone.mobiledevelopment.nutrilens.view.adapter.macros.MakananAdapter
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.pilihan.PilihanMakananActivity
import com.capstone.mobiledevelopment.nutrilens.view.resep.DetailActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory

class HasilMakananActivity : AppCompatActivity() {
    private val viewModel by viewModels<HasilMakananViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var imageUri: Uri
    private var prediction: String? = null
    private var confidence: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_makanan)

        imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) } ?: Uri.EMPTY
        prediction = intent.getStringExtra("prediction")
        confidence = intent.getDoubleExtra("confidence", 0.0)

        val imageView: ImageView = findViewById(R.id.img_makanan)
        val namaMakananTextView: TextView = findViewById(R.id.tv_nama_makanan)

        imageUri.let {
            Glide.with(this)
                .load(it)
                .into(imageView)
        }

        prediction?.let {
            namaMakananTextView.text = it
        }

        val mealTimeLayout: LinearLayout = findViewById(R.id.meal_time_layout)
        val addButton: ImageView = findViewById(R.id.btn_add)
        addButton.setOnClickListener {
            if (mealTimeLayout.visibility == LinearLayout.GONE) {
                mealTimeLayout.visibility = LinearLayout.VISIBLE
            } else {
                mealTimeLayout.visibility = LinearLayout.GONE
            }
        }

        val breakfastButton: ImageButton = findViewById(R.id.breakfast_button)
        val lunchButton: ImageButton = findViewById(R.id.lunch_button)
        val dinnerButton: ImageButton = findViewById(R.id.dinner_button)

        breakfastButton.setOnClickListener { sendMealData("breakfast") }
        lunchButton.setOnClickListener { sendMealData("lunch") }
        dinnerButton.setOnClickListener { sendMealData("dinner") }

        setupView()
        handlePrediction()
    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.isAppearanceLightStatusBars = true // Optional: Set status bar content to dark
        }
        supportActionBar?.hide()

        // Set status bar color to green
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)
    }

    private fun sendMealData(mealType: String) {
        val intent = Intent(this, CatatanMakanan::class.java).apply {
            putExtra("meal_type", mealType)
            putExtra("nama_makanan", prediction)
            // Assuming other nutritional information needs to be sent, you can add those extras here
        }
        startActivity(intent)
    }

    private fun handlePrediction() {
        prediction?.let {
            if (confidence > 70) {
                Toast.makeText(this, "Confidence: $confidence%", Toast.LENGTH_SHORT).show()
            } else {
                showLowConfidenceTooltip()
            }
        }
    }

    private fun showLowConfidenceTooltip() {
        AlertDialog.Builder(this)
            .setTitle("Low Confidence")
            .setMessage("Confidence is low. Do you want to manually input the food?")
            .setPositiveButton("Yes") { dialog, _ ->
                val intent = Intent(this, PilihanMakananActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}