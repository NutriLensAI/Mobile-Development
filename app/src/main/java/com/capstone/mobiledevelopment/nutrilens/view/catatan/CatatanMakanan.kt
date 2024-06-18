package com.capstone.mobiledevelopment.nutrilens.view.catatan

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.DrinkDatabase
import com.capstone.mobiledevelopment.nutrilens.data.reponse.UserFoodResponse
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityCatatanMakananBinding
import com.capstone.mobiledevelopment.nutrilens.view.camera.CameraFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.InputCatatanActivity
import com.capstone.mobiledevelopment.nutrilens.view.login.LoginActivity
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.resep.ResepActivity
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.utils.customview.CustomBottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatatanMakanan : AppCompatActivity() {
    private lateinit var binding: ActivityCatatanMakananBinding
    private val viewModel by viewModels<CatatanMakananViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var isGuestUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatatanMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToggleButtonGroup()
        setupBottomNavigationView()
        setupFab()

        viewModel.fetchToken()
        observeViewModel()

        viewModel.isGuestUser().observe(this) { isGuest ->
            isGuestUser = isGuest
        }

        // Fetch user profile and all meals after token is fetched
        viewModel.token.observe(this) { token ->
            if (token != null) {
                viewModel.fetchUserProfile(token)
                viewModel.fetchAllMeals(token)
            }
        }

        setupView()
        fetchDrinkAndSugarData()
    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.isAppearanceLightStatusBars =
                true // Optional: Set status bar content to dark
        }
        supportActionBar?.hide()

        // Set status bar color to green
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
    }

    private fun observeViewModel() {
        viewModel.allMeals.observe(this) { meals ->
            updateMealsUI(meals)
        }

        viewModel.totalCalories.observe(this) { totalCalories ->
            val calculatedCalories = viewModel.totalCalories.value ?: 2400
            binding.totalCalories.text = "${formatDecimal(totalCalories?.toDouble() ?: 0.0)}/$calculatedCalories Calories"
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateMealsUI(meals: UserFoodResponse) {
        val calculatedCalories = viewModel.totalCalories.value ?: 2400

        // Calculate target grams for each macro
        val targetProteinGrams = (calculatedCalories * 0.15 / 4).toInt()
        val targetCarbsGrams = (calculatedCalories * 0.60 / 4).toInt()
        val targetFatGrams = (calculatedCalories * 0.15 / 9).toInt()

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
            binding.carbsProgressBar.progress =
                ((macros.totalCarbs ?: 0.0) * 100 / targetCarbsGrams).toInt()
            binding.fatProgressBar.progress =
                ((macros.totalFat ?: 0.0) * 100 / targetFatGrams).toInt()
            binding.proteinProgressBar.progress =
                ((macros.totalProteins ?: 0.0) * 100 / targetProteinGrams).toInt()
            val actualCalories = macros.totalCalories ?: 0.0
            binding.totalCalories.text =
                "${formatDecimal(actualCalories)}/$calculatedCalories Calories"

            binding.carbsValueTextView.text =
                formatMacroText(macros.totalCarbs ?: 0.0, targetCarbsGrams)
            binding.fatValueTextView.text = formatMacroText(macros.totalFat ?: 0.0, targetFatGrams)
            binding.proteinValueTextView.text =
                formatMacroText(macros.totalProteins ?: 0.0, targetProteinGrams)
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
                if (isGuestUser && checkedId != R.id.btn_drink) {
                    showLoginDialog()
                } else {
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
    }

    private fun showLoginDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Kamu harus login untuk menggunakan fitur ini")
        builder.setMessage("Silakan login untuk melanjutkan atau pilih Later untuk menggunakan akun guest.")
        builder.setPositiveButton("Login Now") { dialog, _ ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("Later") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onResume() {
        super.onResume()

        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_stats)
        bottomNavigationView.selectedItemId = selectedItemId

        // Fetch user profile and all meals after token is fetched
        viewModel.token.observe(this) { token ->
            if (token != null) {
                viewModel.fetchUserProfile(token)
                viewModel.fetchAllMeals(token)
            }
        }
    }

    private fun setupFab() {
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            if (isGuestUser) {
                showLoginDialog()
            } else {
                val intent = Intent(this@CatatanMakanan, CameraFoodActivity::class.java)
                startActivity(intent)
            }
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
                    val intent = Intent(this@CatatanMakanan, ResepActivity::class.java)
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
