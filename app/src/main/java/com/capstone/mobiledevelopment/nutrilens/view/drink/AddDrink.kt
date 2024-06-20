package com.capstone.mobiledevelopment.nutrilens.view.drink

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.Drink
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.DrinkDatabase
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityAddDrinkBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddDrink : AppCompatActivity() {
    private lateinit var binding: ActivityAddDrinkBinding
    private val drinkDao by lazy { DrinkDatabase.getDatabase(this).drinkDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDrinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupListeners()
    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.isAppearanceLightStatusBars =
                true // Optional: Set status bar content to dark
        }
        supportActionBar?.hide()

        // Set status bar color to green
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)
    }

    private fun setupListeners() {
        binding.addDrinkButton.setOnClickListener {
            val name = binding.drinkNameEditText.text.toString()
            val amount = binding.drinkAmountEditText.text.toString().toIntOrNull() ?: 0
            val sugarUnit = binding.sugarUnitSpinner.selectedItem.toString()
            val sugar = binding.sugarAmountEditText.text.toString().toIntOrNull() ?: 0
            val sugarInGrams =
                if (sugarUnit == getString(R.string.tablespoons)) sugar * 13 else sugar // Assume 1 tablespoon = 13 grams

            if (name.isNotEmpty() && amount > 0) {
                saveDrink(name, amount, sugarInGrams)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.please_fill_in_all_fields_correctly), Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun saveDrink(name: String, amount: Int, sugar: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                drinkDao.insert(Drink(name = name, amount = amount, sugar = sugar))
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddDrink,
                        getString(
                            R.string.successfully_added_with_ml_and_g_of_sugar_to_the_database,
                            name,
                            amount,
                            sugar
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddDrink,
                        "Failed to add drink. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}