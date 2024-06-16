package com.capstone.mobiledevelopment.nutrilens.view.hasil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodResponse
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
    private var matchedNutrition: FoodResponse? = null

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
            val cleanedPrediction = cleanPrediction(it)
            namaMakananTextView.text = cleanedPrediction
            prediction = cleanedPrediction
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

        breakfastButton.setOnClickListener { matchedNutrition?.let { sendMealData("breakfast", it) } }
        lunchButton.setOnClickListener { matchedNutrition?.let { sendMealData("lunch", it) } }
        dinnerButton.setOnClickListener { matchedNutrition?.let { sendMealData("dinner", it) } }

        val viewIngredientsButton: Button = findViewById(R.id.btn_view_ingredients)
        viewIngredientsButton.setOnClickListener { viewIngredients() }

        setupView()
        handlePrediction()

        viewModel.nutritions.observe(this) { nutritionList ->
            matchedNutrition = findMatchingNutrition(nutritionList, prediction)
            if (matchedNutrition == null) {
                showNutritionNotFoundTooltip()
            } else {
                updateNutritionUI(matchedNutrition!!)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Show or hide loading indicator
        }

        // Fetch the nutrition data
        viewModel.fetchNutritions()
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

    private fun sendMealData(mealType: String, nutrition: FoodResponse) {
        val intent = Intent(this, CatatanMakanan::class.java).apply {
            putExtra("meal_type", mealType)
            putExtra("nama_makanan", prediction)
            putExtra("calories", nutrition.calories)
            putExtra("carbs", nutrition.carbohydrate)
            putExtra("fat", nutrition.fat)
            putExtra("protein", nutrition.proteins)
        }
        startActivity(intent)
    }

    private fun handlePrediction() {
        prediction?.let {
            if (confidence > 70) {
                Toast.makeText(this, "Confidence: $confidence%", Toast.LENGTH_SHORT).show()
            } else  {
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

    private fun updateNutritionUI(nutrition: FoodResponse) {
        // Update TextViews
        findViewById<TextView>(R.id.tv_carbs_value).text = "${nutrition.carbohydrate ?: 0.0} g"
        findViewById<TextView>(R.id.tv_fat_value).text = "${nutrition.fat ?: 0.0} g"
        findViewById<TextView>(R.id.tv_protein_value).text = "${nutrition.proteins ?: 0.0} g"

        // Update ProgressBars
        findViewById<ProgressBar>(R.id.carbsProgressBar).progress =
            nutrition.carbohydrate.toInt()
        findViewById<ProgressBar>(R.id.fatProgressBar).progress =
            nutrition.fat.toInt()
        findViewById<ProgressBar>(R.id.proteinProgressBar).progress =
            nutrition.proteins.toInt()

        // Update Grid TextViews
        findViewById<TextView>(R.id.tv_carbs_value_grid).text = "${nutrition.carbohydrate ?: 0.0} gr"
        findViewById<TextView>(R.id.tv_fat_value_grid).text = "${nutrition.fat ?: 0.0} gr"
        findViewById<TextView>(R.id.tv_protein_value_grid).text = "${nutrition.proteins ?: 0.0} gr"
        findViewById<TextView>(R.id.tv_calories_value_grid).text = "${nutrition.calories ?: 0.0} kcal"
    }

    private fun findMatchingNutrition(
        nutritionList: List<FoodResponse>,
        prediction: String?
    ): FoodResponse? {
        return nutritionList.find {
            it.name.contains(prediction ?: "", ignoreCase = true)
        }
    }

    private fun showNutritionNotFoundTooltip() {
        AlertDialog.Builder(this)
            .setTitle("Nutrition Not Found")
            .setMessage("The nutrition information for the predicted food item was not found in the database.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun cleanPrediction(prediction: String): String {
        return prediction.replace(Regex("[^A-Za-z0-9 ]"), " ").trim().split("\\s+".toRegex()).take(2).joinToString(" ")
    }

    private fun viewIngredients() {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("image_uri", imageUri.toString())
            putExtra("prediction", prediction)
            putExtra("confidence", confidence)
            // If matchedNutrition is not null, pass its details as well
            matchedNutrition?.let {
                putExtra("carbs", it.carbohydrate)
                putExtra("fat", it.fat)
                putExtra("protein", it.proteins)
                putExtra("calories", it.calories)
            }
        }
        startActivity(intent)
    }
}