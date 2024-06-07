package com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.step.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMyRecipes : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etIngredients: EditText
    private lateinit var etSteps: EditText
    private lateinit var btnSave: Button
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_my_recipes)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "nutrilens-db"
        ).build()

        etTitle = findViewById(R.id.etTitle)
        etIngredients = findViewById(R.id.etIngredients)
        etSteps = findViewById(R.id.etSteps)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            addRecipeToDatabase()
        }
    }

    private fun addRecipeToDatabase() {
        val title = etTitle.text.toString()
        val ingredients = etIngredients.text.toString()
        val steps = etSteps.text.toString()

        if (title.isNotEmpty() && ingredients.isNotEmpty() && steps.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
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
            }
        }
    }
}
