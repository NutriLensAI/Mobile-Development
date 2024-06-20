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
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.FoodRequest
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodResponse
import com.capstone.mobiledevelopment.nutrilens.view.pilihan.PilihanMakananActivity
import com.capstone.mobiledevelopment.nutrilens.view.resep.DetailActivity
import com.capstone.mobiledevelopment.nutrilens.view.resep.ResepItem
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.io.IOException

class HasilMakananActivity : AppCompatActivity() {
    private val viewModel by viewModels<HasilMakananViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var imageUri: Uri
    private var prediction: String? = null
    private var confidence: Double = 0.0
    private var matchedNutrition: FoodResponse? = null
    private var matchedRecipe: ResepItem? = null

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
            namaMakananTextView.text = it.trim()
        }

        val mealTimeLayout: LinearLayout = findViewById(R.id.meal_time_layout)
        val addButton: ImageView = findViewById(R.id.btn_add)
        addButton.setOnClickListener {
            mealTimeLayout.visibility = if (mealTimeLayout.visibility == LinearLayout.GONE) {
                LinearLayout.VISIBLE
            } else {
                LinearLayout.GONE
            }
        }

        val breakfastButton: ImageButton = findViewById(R.id.breakfast_button)
        val lunchButton: ImageButton = findViewById(R.id.lunch_button)
        val dinnerButton: ImageButton = findViewById(R.id.dinner_button)

        breakfastButton.setOnClickListener { addFoodToMeal("breakfasts") }
        lunchButton.setOnClickListener { addFoodToMeal("lunchs") }
        dinnerButton.setOnClickListener { addFoodToMeal("dinners") }

        val viewIngredientsButton: Button = findViewById(R.id.btn_view_ingredients)
        viewIngredientsButton.setOnClickListener { viewIngredients() }

        setupView()
        handlePrediction()

        viewModel.nutritions.observe(this) { nutritionList ->
            matchedNutrition = findMatchingNutrition(nutritionList, prediction)
            if (matchedNutrition == null) {
                showNutritionNotFoundTooltip()
            }
            if (matchedNutrition != null) {
                updateNutritionUI(matchedNutrition!!)
            }
        }

        // Load recipes from local JSON file
        val recipes = loadRecipesFromAssets()
        matchedRecipe = findMatchingRecipe(recipes, prediction)
        if (matchedRecipe == null) {
            showRecipeNotFoundTooltip()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Show or hide loading indicator
        }

        // Fetch the nutrition data
        viewModel.fetchNutritions()
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
        window.navigationBarColor =
            ContextCompat.getColor(this, android.R.color.white) // Set navigation bar color to white
    }

    private fun addFoodToMeal(table: String) {
        val token = viewModel.token.value ?: return
        val food = matchedNutrition
        if (food != null) {
            val foodRequest = FoodRequest(
                id = 0,
                user_id = 0,
                food_id = food.id,
                food_name = food.name,
                calories = food.calories,
                proteins = food.proteins,
                fat = food.fat,
                carbohydrate = food.carbohydrate
            )
            viewModel.addFoodToMeal(token, table, food.id, foodRequest)
            Toast.makeText(
                this,
                getString(R.string.makanan_kamu_berhasil_ditambahkan), Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.nutrisi_makanan_kamu_tidak_ada_di_database_kami),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handlePrediction() {
        prediction?.let {
            if (confidence > 70) {
                Toast.makeText(this, getString(R.string.confidence, confidence), Toast.LENGTH_SHORT)
                    .show()
            } else {
                showLowConfidenceTooltip()
            }
        }
    }

    private fun showLowConfidenceTooltip() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.low_confidence))
            .setMessage(getString(R.string.confidence_is_low_do_you_want_to_manually_input_the_food))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
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
        val totalCalories = nutrition.calories ?: 0.0
        val carbsCalories = (nutrition.carbohydrate ?: 0.0) * 4
        val fatCalories = (nutrition.fat ?: 0.0) * 9
        val proteinCalories = (nutrition.proteins ?: 0.0) * 4

        val carbsPercentage =
            if (totalCalories > 0) (carbsCalories / totalCalories * 100).toInt() else 0
        val fatPercentage =
            if (totalCalories > 0) (fatCalories / totalCalories * 100).toInt() else 0
        val proteinPercentage =
            if (totalCalories > 0) (proteinCalories / totalCalories * 100).toInt() else 0

        findViewById<TextView>(R.id.tv_carbs_value).text = "${nutrition.carbohydrate ?: 0.0} g"
        findViewById<TextView>(R.id.tv_fat_value).text = "${nutrition.fat ?: 0.0} g"
        findViewById<TextView>(R.id.tv_protein_value).text = "${nutrition.proteins ?: 0.0} g"
        findViewById<TextView>(R.id.tv_calories_value).text = "${totalCalories} KKal"

        findViewById<ProgressBar>(R.id.carbsProgressBar).progress = carbsPercentage
        findViewById<ProgressBar>(R.id.fatProgressBar).progress = fatPercentage
        findViewById<ProgressBar>(R.id.proteinProgressBar).progress = proteinPercentage
    }

    private fun findMatchingNutrition(
        nutritionList: List<FoodResponse>,
        prediction: String?
    ): FoodResponse? {
        return nutritionList.find {
            it.name.contains(prediction?.trim() ?: "", ignoreCase = true)
        }
    }

    private fun findMatchingRecipe(
        recipeList: List<ResepItem>,
        prediction: String?
    ): ResepItem? {
        return recipeList.find {
            it.Title.contains(prediction?.trim() ?: "", ignoreCase = true)
        }
    }

    private fun showNutritionNotFoundTooltip() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.nutrition_not_found))
            .setMessage(getString(R.string.the_nutrition_information_for_the_predicted_food_item_was_not_found_in_the_database))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showRecipeNotFoundTooltip() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.recipe_not_found))
            .setMessage(getString(R.string.the_recipe_information_for_the_predicted_food_item_was_not_found_in_the_database))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun viewIngredients() {
        matchedRecipe?.let {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_TITLE", it.Title)
                putExtra("EXTRA_INGREDIENTS", it.Ingredients)
                putExtra("EXTRA_STEPS", it.Steps)
            }
            startActivity(intent)
        } ?: run {
            showRecipeNotFoundTooltip()
        }
    }

    private fun loadRecipesFromAssets(): List<ResepItem> {
        val jsonString: String
        try {
            jsonString = assets.open("datarecipe.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }

        val jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)
        val recipeArray = jsonObject.getAsJsonArray("recipeData")

        val listRecipeType = object : TypeToken<List<ResepItem>>() {}.type
        return Gson().fromJson(recipeArray, listRecipeType)
    }
}
