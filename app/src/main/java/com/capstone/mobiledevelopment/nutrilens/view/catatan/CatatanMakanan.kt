package com.capstone.mobiledevelopment.nutrilens.view.catatan

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityCatatanMakananBinding
import com.capstone.mobiledevelopment.nutrilens.view.resep.Resep
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodItem
import com.capstone.mobiledevelopment.nutrilens.view.camera.CameraFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.InputCatatanActivity
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
    }




    private fun updateMacros(foodData: List<FoodItem>) {
        val totalCarbs = foodData.sumOf { it.carbs }
        val totalFat = foodData.sumOf { it.fat }
        val totalProtein = foodData.sumOf { it.protein }
        val totalCalories = foodData.sumOf { it.calories }

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
