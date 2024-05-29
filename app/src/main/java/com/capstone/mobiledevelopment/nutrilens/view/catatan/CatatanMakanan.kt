package com.capstone.mobiledevelopment.nutrilens.view.catatan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityCatatanMakananBinding
import com.capstone.mobiledevelopment.nutrilens.view.adapter.FoodAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.FoodItem
import com.capstone.mobiledevelopment.nutrilens.view.addfood.AddFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.pilihan.PilihanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CatatanMakanan : AppCompatActivity() {
    private lateinit var binding: ActivityCatatanMakananBinding
    private lateinit var foodList: MutableList<FoodItem>
    private lateinit var adapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatatanMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        foodList = mutableListOf(
            FoodItem("Breakfast", 14, 14, 14, 500, mutableListOf(FoodItem.FoodDetail("Nasi Gudeg Rawon", 14, 14, 14, 500))),
            FoodItem("Lunch", 14, 14, 14, 500, mutableListOf(FoodItem.FoodDetail("Nasi Gudeg Rawon", 14, 14, 14, 500))),
            FoodItem("Dinner", 14, 14, 14, 500, mutableListOf(FoodItem.FoodDetail("Nasi Gudeg Rawon", 14, 14, 14, 500)))
        )

        adapter = FoodAdapter(foodList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.carbsProgressBar.progress = 75
        binding.fatProgressBar.progress = 100
        binding.proteinProgressBar.progress = 100
        binding.carbsValueTextView.text = "75/100 g"
        binding.fatValueTextView.text = "100/100 g"
        binding.proteinValueTextView.text = "100/100 g"
        binding.totalCalories.text = "1500/2400 Calories"

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
                    val intent = Intent(this@CatatanMakanan, PilihanMakanan::class.java)
                    intent.putExtra("selected_item", R.id.navigation_food)
                    startActivity(intent)
                    true
                }

                R.id.navigation_profile -> {
                    val intent = Intent(this@CatatanMakanan, SettingsActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_profile)
                    startActivity(intent)
                    true
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
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@CatatanMakanan, AddFoodActivity::class.java)
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
            val newFoodItem = FoodItem(mealType, carbs, fat, protein, calories, mutableListOf(FoodItem.FoodDetail(namaMakanan, carbs, fat, protein, calories)))
            foodList.add(newFoodItem)
        }
        adapter.notifyDataSetChanged()

        // Update the macros
        updateMacros()
    }

    private fun updateMacros() {
        val totalCarbs = foodList.sumOf { it.carbs }
        val totalFat = foodList.sumOf { it.fat }
        val totalProtein = foodList.sumOf { it.protein }
        val totalCalories = foodList.sumOf { it.calories }

        binding.carbsProgressBar.progress = totalCarbs
        binding.fatProgressBar.progress = totalFat
        binding.proteinProgressBar.progress = totalProtein
        binding.carbsValueTextView.text = "$totalCarbs/100 g"
        binding.fatValueTextView.text = "$totalFat/100 g"
        binding.proteinValueTextView.text = "$totalProtein/100 g"
        binding.totalCalories.text = "$totalCalories/2400 Calories"
    }
}
