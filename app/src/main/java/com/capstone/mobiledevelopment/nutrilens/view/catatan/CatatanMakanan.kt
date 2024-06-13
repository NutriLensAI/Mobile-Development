package com.capstone.mobiledevelopment.nutrilens.view.catatan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.DrinkDatabase
import com.capstone.mobiledevelopment.nutrilens.data.reponse.UserFoodResponse
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityCatatanMakananBinding
import com.capstone.mobiledevelopment.nutrilens.view.resep.Resep
import com.capstone.mobiledevelopment.nutrilens.view.camera.CameraFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.InputCatatanActivity
import com.capstone.mobiledevelopment.nutrilens.view.drink.AddDrink
import com.capstone.mobiledevelopment.nutrilens.view.utils.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        setupView()
        fetchDrinkAndSugarData()
    }
    private fun setupView() {
        // Ensure the content fits system windows to avoid shifting
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Make status bar transparent
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT // Ensure the status bar is transparent

        // Optionally set status bar content to dark
        WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
            controller.isAppearanceLightStatusBars = true
        }

        // Hide the action bar if any
        supportActionBar?.hide()
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
        val targetProteinGrams = (totalCalories * 0.15 / 4).toInt()
        val targetCarbsGrams = (totalCalories * 0.60 / 4).toInt()
        val targetFatGrams = (totalCalories * 0.15 / 9).toInt()
        // Update Breakfast
        meals.breakfast?.let { breakfast ->
            binding.breakfastCarbs.text = formatDecimal(breakfast.total?.carbs ?: 0.0)
            binding.breakfastFat.text = formatDecimal(breakfast.total?.fat ?: 0.0)
            binding.breakfastProtein.text = formatDecimal(breakfast.total?.prot ?: 0.0)
            binding.breakfastCalories.text = formatDecimal(breakfast.total?.calories ?: 0.0)
        }

        // Update Lunch
        meals.lunch?.let { lunch ->
            binding.lunchCarbs.text = formatDecimal(lunch.total?.carbs ?: 0.0)
            binding.lunchFat.text = formatDecimal(lunch.total?.fat ?: 0.0)
            binding.lunchProtein.text = formatDecimal(lunch.total?.prot ?: 0.0)
            binding.lunchCalories.text = formatDecimal(lunch.total?.calories ?: 0.0)
        }

        // Update Dinner
        meals.dinner?.let { dinner ->
            binding.dinnerCarbs.text = formatDecimal(dinner.total?.carbs ?: 0.0)
            binding.dinnerFat.text = formatDecimal(dinner.total?.fat ?: 0.0)
            binding.dinnerProtein.text = formatDecimal(dinner.total?.prot ?: 0.0)
            binding.dinnerCalories.text = formatDecimal(dinner.total?.calories ?: 0.0)
        }

        // Update Macros
        meals.macros?.let { macros ->
            binding.carbsProgressBar.progress = ((macros.totalCarbs ?: 0.0) * 100 / targetCarbsGrams).toInt()
            binding.fatProgressBar.progress = ((macros.totalFat ?: 0.0) * 100 / targetFatGrams).toInt()
            binding.proteinProgressBar.progress = ((macros.totalProteins ?: 0.0) * 100 / targetProteinGrams).toInt()
            binding.totalCalories.text = "${formatDecimal(macros.totalCalories ?: 0.0)}/$totalCalories Calories"

            binding.carbsValueTextView.text = formatMacroText(macros.totalCarbs ?: 0.0, targetCarbsGrams)
            binding.fatValueTextView.text = formatMacroText(macros.totalFat ?: 0.0, targetFatGrams)
            binding.proteinValueTextView.text = formatMacroText(macros.totalProteins ?: 0.0, targetProteinGrams)
        }
    }

    private fun formatMacroText(value: Double, target: Int): String {
        return "${formatDecimal(value)}\nof\n$target g"
    }

    private fun formatDecimal(value: Double): String {
        return String.format("%.2f", value)
    }

    private fun fetchDrinkAndSugarData() {
        val drinkDao = DrinkDatabase.getDatabase(this).drinkDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val totalDrinkAmount = drinkDao.getTotalAmount() ?: 0
            val totalSugarAmount = drinkDao.getTotalSugarAmount() ?: 0
            withContext(Dispatchers.Main) {
                binding.drinkMililiter.text = "$totalDrinkAmount ml"
                binding.drinkSugar.text = "$totalSugarAmount g"
            }
        }
    }

    private fun setupToggleButtonGroup() {
        binding.toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val selectedFragment = when (checkedId) {
                    R.id.btn_breakfast -> "BREAKFAST"
                    R.id.btn_lunch -> "LUNCH"
                    R.id.btn_dinner -> "DINNER"
                    R.id.btn_drink -> {
                        val intent = Intent(this, AddDrink::class.java)
                        startActivity(intent)
                        return@addOnButtonCheckedListener
                    }
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
