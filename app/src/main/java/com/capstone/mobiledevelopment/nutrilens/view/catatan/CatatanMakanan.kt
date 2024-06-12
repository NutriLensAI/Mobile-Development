package com.capstone.mobiledevelopment.nutrilens.view.catatan

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.reponse.UserFoodResponse
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityCatatanMakananBinding
import com.capstone.mobiledevelopment.nutrilens.view.resep.Resep
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodItem
import com.capstone.mobiledevelopment.nutrilens.view.camera.CameraFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.InputCatatanActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.breakfast.BreakfastFragment
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.dinner.DinnerFragment
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.lunch.LunchFragment
import com.capstone.mobiledevelopment.nutrilens.view.login.LoginViewModel
import com.capstone.mobiledevelopment.nutrilens.view.utils.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.main.MainViewModel
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CatatanMakanan : AppCompatActivity() {
    private lateinit var binding: ActivityCatatanMakananBinding
    private val viewModel by viewModels<CatatanMakananViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatatanMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToggleButtonGroup()
        setupBottomNavigationView()
        setupFab()

        viewModel.fetchToken()
        observeViewModel()

        // Fetch all meals after token is fetched
        viewModel.token.observe(this) { token ->
            if (token != null) {
                viewModel.fetchAllMeals()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.allMeals.observe(this) { meals ->
            updateMealsUI(meals)
        }

        viewModel.totalCalories.observe(this) { totalCalories ->
            binding.totalCalories.text = "$totalCalories Calories"
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateMealsUI(meals: UserFoodResponse) {
        val totalCalories = viewModel.totalCalories.value ?: 2400

        // Calculate target grams for each macro
        val targetProteinGrams = (totalCalories * 0.20 / 4).toInt()
        val targetCarbsGrams = (totalCalories * 0.50 / 4).toInt()
        val targetFatGrams = (totalCalories * 0.30 / 9).toInt()

        // Update Breakfast
        meals.breakfast?.let { breakfast ->
            binding.breakfastCarbs.text = breakfast.total?.carbs.toString()
            binding.breakfastFat.text = breakfast.total?.fat.toString()
            binding.breakfastProtein.text = breakfast.total?.prot.toString()
            binding.breakfastCalories.text = breakfast.total?.calories.toString()
        }

        // Update Lunch
        meals.lunch?.let { lunch ->
            binding.lunchCarbs.text = lunch.total?.carbs.toString()
            binding.lunchFat.text = lunch.total?.fat.toString()
            binding.lunchProtein.text = lunch.total?.prot.toString()
            binding.lunchCalories.text = lunch.total?.calories.toString()
        }

        // Update Dinner
        meals.dinner?.let { dinner ->
            binding.dinnerCarbs.text = dinner.total?.carbs.toString()
            binding.dinnerFat.text = dinner.total?.fat.toString()
            binding.dinnerProtein.text = dinner.total?.prot.toString()
            binding.dinnerCalories.text = dinner.total?.calories.toString()
        }

        // Update Macros
        meals.macros?.let { macros ->
            binding.carbsProgressBar.progress = (macros.totalCarbs ?: 0) * 100 / targetCarbsGrams
            binding.fatProgressBar.progress = (macros.totalFat ?: 0) * 100 / targetFatGrams
            binding.proteinProgressBar.progress = (macros.totalProteins ?: 0) * 100 / targetProteinGrams
            binding.totalCalories.text = "${macros.totalCalories ?: 0}/$totalCalories Calories"

            binding.carbsValueTextView.text = formatMacroText(macros.totalCarbs ?: 0, targetCarbsGrams)
            binding.fatValueTextView.text = formatMacroText(macros.totalFat ?: 0, targetFatGrams)
            binding.proteinValueTextView.text = formatMacroText(macros.totalProteins ?: 0, targetProteinGrams)
        }
    }

    private fun formatMacroText(value: Int, target: Int): String {
        return "$value\nof\n$target g"
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

                val mealData: Parcelable? = when (selectedFragment) {
                    "BREAKFAST" -> viewModel.allMeals.value?.breakfast
                    "LUNCH" -> viewModel.allMeals.value?.lunch
                    "DINNER" -> viewModel.allMeals.value?.dinner
                    else -> null
                }

                val intent = Intent(this, InputCatatanActivity::class.java).apply {
                    putExtra("selected_fragment", selectedFragment)
                    putExtra("selected_meal", mealData)
                }
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

    private fun setupBottomNavigationView() {
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
                    val totalCalories = viewModel.totalCalories.value ?: 2400
                    val macros = viewModel.allMeals.value?.macros
                    val intent = Intent(this@CatatanMakanan, MainActivity::class.java).apply {
                        putExtra("selected_item", R.id.navigation_stats)
                        putExtra("total_calories", totalCalories)
                        putExtra("macros", macros)
                    }
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
    }
}