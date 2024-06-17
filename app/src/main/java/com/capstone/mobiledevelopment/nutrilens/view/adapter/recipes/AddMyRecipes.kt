package com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMyRecipes : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etIngredients: EditText
    private lateinit var etSteps: EditText
    private lateinit var btnSave: Button
    private lateinit var db: StepDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_my_recipes)

        db = StepDatabase.getDatabase(applicationContext)

        etTitle = findViewById(R.id.etTitle)
        etIngredients = findViewById(R.id.etIngredients)
        etSteps = findViewById(R.id.etSteps)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            addRecipeToDatabase()
        }
        setupView()
    }

    private fun addRecipeToDatabase() {
        val title = etTitle.text.toString()
        val ingredients = etIngredients.text.toString()
        val steps = etSteps.text.toString()

        if (title.isNotEmpty() && ingredients.isNotEmpty() && steps.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    db.myRecipeDao().insertRecipe(
                        MyRecipe(
                            title = title,
                            ingredients = ingredients,
                            steps = steps
                        )
                    )
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("AddMyRecipes", "Error inserting recipe", e)
                }
            }
        } else {
            Log.w("AddMyRecipes", "Title, Ingredients, or Steps are empty")
        }
    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.isAppearanceLightStatusBars =
                true // Optional: Set status bar content to dark
        }
        supportActionBar?.hide()

        // Set status bar color to green
        window.statusBarColor = ContextCompat.getColor(this, R.color.green2)
    }
}
