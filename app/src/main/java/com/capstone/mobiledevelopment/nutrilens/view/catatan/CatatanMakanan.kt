package com.capstone.mobiledevelopment.nutrilens.view.catatan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityCatatanMakananBinding
import com.capstone.mobiledevelopment.nutrilens.view.resep.Resep
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodItem
import com.capstone.mobiledevelopment.nutrilens.view.camera.CameraFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.InputCatatanActivity
import com.capstone.mobiledevelopment.nutrilens.view.drink.AddDrink
import com.capstone.mobiledevelopment.nutrilens.view.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CatatanMakanan : AppCompatActivity() {
    private lateinit var binding: ActivityCatatanMakananBinding
    private lateinit var foodList: MutableList<FoodItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatatanMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToggleButtonGroup()

        foodList = mutableListOf(
            FoodItem("Breakfast", 14, 14, 14, 500, mutableListOf(FoodItem.FoodDetail("Nasi Gudeg Rawon", 14, 14, 14, 500))),
            FoodItem("Lunch", 14, 14, 14, 500, mutableListOf(FoodItem.FoodDetail("Nasi Gudeg Rawon", 14, 14, 14, 500))),
            FoodItem("Dinner", 14, 14, 14, 500, mutableListOf(FoodItem.FoodDetail("Nasi Gudeg Rawon", 14, 14, 14, 500)))
        )

        updateMacros()

        val mealType = intent.getStringExtra("meal_type")
        val namaMakanan = intent.getStringExtra("nama_makanan")
        val calories = intent.getIntExtra("calories", 0)
        val carbs = intent.getIntExtra("carbs", 0)
        val fat = intent.getIntExtra("fat", 0)
        val protein = intent.getIntExtra("protein", 0)

        if (mealType != null && namaMakanan != null) {
            addFoodToMeal(mealType, namaMakanan, calories, carbs, fat, protein)
        }

        // Initialize the custom bottom navigation view
        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu)

        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_stats)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_food -> {
                    val intent = Intent(this@CatatanMakanan, Resep::class.java)
                    intent.putExtra("selected_item", R.id.navigation_food)
                    startActivity(intent)
                    true
                }

                R.id.navigation_profile -> {
                    val intent = Intent(this@CatatanMakanan, SettingsActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_profile)
                    startActivity(intent)
                    false
                }

                R.id.navigation_stats -> {
                    val intent = Intent(this@CatatanMakanan, MainActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_stats)
                    startActivity(intent)
                    true
                }

                R.id.navigation_documents -> {
                    // Activity ini sudah halaman statistik
                    true
                }

                else -> false
            }
        }

        setupFab()
    }

    private fun setupToggleButtonGroup() {
        binding.toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val selectedFragment = when (checkedId) {
                    R.id.btn_breakfast -> "BREAKFAST"
                    R.id.btn_lunch -> "LUNCH"
                    R.id.btn_dinner -> "DINNER"
                    R.id.btn_drink -> "DRINK"
                    else -> "BREAKFAST"
                }
                val intent = Intent(this, InputCatatanActivity::class.java)
                intent.putExtra("selected_fragment", selectedFragment)
                startActivity(intent)
            }
        }
    }

    private fun setupFab() {
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@CatatanMakanan, CameraFoodActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addFoodToMeal(mealType: String, namaMakanan: String, calories: Int, carbs: Int, fat: Int, protein: Int) {
        val existingFoodItem = foodList.find { it.mealTitle.equals(mealType, ignoreCase = true) }
        if (existingFoodItem != null) {
            existingFoodItem.foodItems.add(FoodItem.FoodDetail(namaMakanan, carbs, fat, protein, calories))
            existingFoodItem.carbs += carbs
            existingFoodItem.fat += fat
            existingFoodItem.protein += protein
            existingFoodItem.calories += calories
        } else {
            val newFoodItem = FoodItem(mealType, carbs, fat, protein, calories, mutableListOf(
                FoodItem.FoodDetail(namaMakanan, carbs, fat, protein, calories)))
            foodList.add(newFoodItem)
        }

        updateSection(mealType)
        updateMacros()
    }

    private fun updateSection(mealType: String) {
        val foodItem = foodList.find { it.mealTitle.equals(mealType, ignoreCase = true) } ?: return
        when (mealType.lowercase()) {
            "breakfast" -> {
                binding.breakfastCarbs.text = "${foodItem.carbs} g"
                binding.breakfastFat.text = "${foodItem.fat} g"
                binding.breakfastProtein.text = "${foodItem.protein} g"
                binding.breakfastCalories.text = "${foodItem.calories}"
            }
            "lunch" -> {
                binding.lunchCarbs.text = "${foodItem.carbs} g"
                binding.lunchFat.text = "${foodItem.fat} g"
                binding.lunchProtein.text = "${foodItem.protein} g"
                binding.lunchCalories.text = "${foodItem.calories}"
            }
            "dinner" -> {
                binding.dinnerCarbs.text = "${foodItem.carbs} g"
                binding.dinnerFat.text = "${foodItem.fat} g"
                binding.dinnerProtein.text = "${foodItem.protein} g"
                binding.dinnerCalories.text = "${foodItem.calories}"
            }
        }
    }

    private fun updateMacros() {
        val totalCarbs = foodList.sumOf { it.carbs }
        val totalFat = foodList.sumOf { it.fat }
        val totalProtein = foodList.sumOf { it.protein }
        val totalCalories = foodList.sumOf { it.calories }

        binding.carbsProgressBar.progress = totalCarbs
        binding.fatProgressBar.progress = totalFat
        binding.proteinProgressBar.progress = totalProtein

        binding.carbsValueTextView.text = formatMacroText(totalCarbs, 100)
        binding.fatValueTextView.text = formatMacroText(totalFat, 100)
        binding.proteinValueTextView.text = formatMacroText(totalProtein, 100)
        binding.totalCalories.text = "$totalCalories/2400 Calories"
    }

    private fun formatMacroText(value: Int, max: Int): String {
        return "$value\nof\n$max g"
    }

}